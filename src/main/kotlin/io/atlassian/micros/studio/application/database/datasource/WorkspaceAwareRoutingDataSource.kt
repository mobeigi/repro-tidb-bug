package io.atlassian.micros.studio.application.database.datasource

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.common.feature.service.FeatureGates
import io.atlassian.micros.studio.common.feature.service.FeatureManagementService
import io.atlassian.micros.studio.common.workspace.exception.MissingWorkspaceIdHeaderException
import io.atlassian.micros.studio.config.spring.AttributeConstants
import io.atlassian.micros.studio.domain.tcs.service.TcsService
import io.atlassian.micros.studio.domain.tms.service.TmsService
import org.springframework.jdbc.datasource.AbstractDataSource
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.sql.Connection
import java.sql.SQLException
import java.time.Duration
import javax.sql.DataSource

abstract class WorkspaceAwareRoutingDataSource(
    private val tcsService: TcsService,
    private val tmsService: TmsService,
    private val dataSourceId: String,
    private val featureManagementService: FeatureManagementService,
) : AbstractDataSource() {
    companion object {
        const val SQL_INVALID_AUTHORIZATION_SPEC_EXCEPTION_STATE_ID = "28000"
    }

    private val dedupEnabledDataSources: Cache<WorkspaceId, DataSource> =
        Caffeine
            .newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofHours(12))
            .build()

    private val dedupDisabledDataSources: Cache<WorkspaceId, DataSource> =
        Caffeine
            .newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofHours(12))
            .build()

    protected fun resolveCache(dedupEnabled: Boolean): Cache<WorkspaceId, DataSource> =
        if (dedupEnabled) dedupEnabledDataSources else dedupDisabledDataSources

    override fun getConnection(): Connection {
        val workspaceId = determineCurrentLookupKey()
        val dedupEnabled = isDedupEnabled(workspaceId)
        val cache = resolveCache(dedupEnabled)
        return try {
            var cacheMiss = false
            val dataSource =
                cache.get(workspaceId) { key ->
                    cacheMiss = true
                    createDataSourceForWorkspace(key)
                }
            dataSource.connection
        } catch (e: Exception) {
            // Clear cache in case of outdated credentials to enable retry: https://developer.atlassian.com/platform/tdpsql/how-to-guides/connecting-via-jdbc/#steps
            if (e is SQLException && e.sqlState == SQL_INVALID_AUTHORIZATION_SPEC_EXCEPTION_STATE_ID) {
                cache.invalidate(workspaceId)

                return try {
                    val dataSourceRetry = createDataSourceForWorkspace(workspaceId)
                    cache.put(workspaceId, dataSourceRetry)
                    dataSourceRetry.connection
                } catch (retryException: Exception) {
                    throw retryException
                }
            }
            throw e
        }
    }

    fun isDedupEnabled(workspaceId: WorkspaceId): Boolean =
        featureManagementService.isFeatureEnabled(FeatureGates.DEDUP_JDBC, workspaceId, null)

    override fun getConnection(
        username: String?,
        password: String?,
    ): Connection = throw UnsupportedOperationException("Not implemented")

    protected fun determineCurrentLookupKey(): WorkspaceId =
        RequestContextHolder
            .currentRequestAttributes()
            .getAttribute(AttributeConstants.WORKSPACE_ID, RequestAttributes.SCOPE_REQUEST)
            as? WorkspaceId
            ?: throw MissingWorkspaceIdHeaderException()

    private fun getCredentialsUrl(workspaceId: WorkspaceId): String = tcsService.getTdpSqlTransformerRecord(workspaceId).credentialsUrl

    protected fun getTenantMetadata(workspaceId: WorkspaceId) = tmsService.getDecryptedTenantMetadata(getCredentialsUrl(workspaceId))

    abstract fun createDataSourceForWorkspace(workspaceId: WorkspaceId): DataSource
}
