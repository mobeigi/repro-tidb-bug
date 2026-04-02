package io.atlassian.micros.studio.application.database.datasource

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.common.feature.service.FeatureManagementService
import io.atlassian.micros.studio.domain.tcs.service.TcsService
import io.atlassian.micros.studio.domain.tms.service.TmsService
import io.micrometer.core.instrument.MeterRegistry
import java.sql.Connection
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

class DdlWorkspaceAwareRoutingDataSource(
    tcsService: TcsService,
    tmsService: TmsService,
    dataSourceId: String,
    private val meterRegistry: MeterRegistry,
    featureManagementService: FeatureManagementService,
) : WorkspaceAwareRoutingDataSource(tcsService, tmsService, dataSourceId, featureManagementService) {
    private val sharedPools: Cache<ConnectionKey, DataSource> =
        Caffeine
            .newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofHours(12))
            .build()

    private val databaseNames = ConcurrentHashMap<WorkspaceId, String>()

    override fun getConnection(): Connection {
        val connection = super.getConnection()
        val workspaceId = determineCurrentLookupKey()
        if (isDedupEnabled(workspaceId)) {
            connection.catalog = databaseNames[workspaceId]
        }
        return connection
    }

    override fun createDataSourceForWorkspace(workspaceId: WorkspaceId): DataSource {
        val (_, databaseName, ddlUserMetadata) = getTenantMetadata(workspaceId)

        if (isDedupEnabled(workspaceId)) {
            databaseNames[workspaceId] = databaseName

            val connectionKey =
                ConnectionKey(
                    host = ddlUserMetadata.host,
                    port = ddlUserMetadata.port,
                    username = ddlUserMetadata.username,
                    password = ddlUserMetadata.password,
                )

            return sharedPools.get(connectionKey) {
                HikariDataSource(
                    HikariConfig().apply {
                        jdbcUrl = "jdbc:mysql://${it.host}:${it.port}/"
                        username = it.username
                        password = it.password

                        // Connection settings for shared DDL operations
                        maximumPoolSize = 10
                        minimumIdle = 1
                        connectionTimeout = 10_000
                        idleTimeout = 300_000
                        leakDetectionThreshold = 60_000

                        // Monitoring
                        poolName = "DDL-Pool-${it.host}-${it.port}"
                        metricRegistry = this@DdlWorkspaceAwareRoutingDataSource.meterRegistry
                    },
                )
            }
        } else {
            val config =
                HikariConfig().apply {
                    jdbcUrl = "jdbc:mysql://${ddlUserMetadata.host}:${ddlUserMetadata.port}/$databaseName"
                    username = ddlUserMetadata.username
                    password = ddlUserMetadata.password

                    // Connection settings for per workspace DDL operations
                    maximumPoolSize = 3
                    minimumIdle = 0
                    connectionTimeout = 30_000
                    idleTimeout = 600_000
                    leakDetectionThreshold = 120_000

                    // Monitoring
                    poolName = "DDL-Pool-$workspaceId"
                    metricRegistry = this@DdlWorkspaceAwareRoutingDataSource.meterRegistry
                }

            return HikariDataSource(config)
        }
    }
}
