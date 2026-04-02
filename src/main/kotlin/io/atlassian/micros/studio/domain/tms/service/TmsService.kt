package io.atlassian.micros.studio.domain.tms.service

import io.atlassian.micros.studio.domain.tms.model.DecryptedTenantMetadata
import org.springframework.stereotype.Service

/**
 * Stub TmsService that returns hardcoded local database credentials.
 * Replaces the Atlassian-internal TMS HTTP client to allow the repo to build
 * from open-source dependencies only.
 *
 * The credentials here match what IntegrationTestDataSourceConfig uses so that
 * the integration tests can exercise the real datasource routing code path.
 */
@Service
class TmsService {
    fun getDecryptedTenantMetadata(credentialsUrl: String): DecryptedTenantMetadata =
        DecryptedTenantMetadata(
            status = "ACTIVE",
            databaseName = "test",
            ddlUserMetadata =
                DecryptedTenantMetadata.DdlUserMetadata(
                    host = "localhost",
                    port = "4000",
                    username = "root",
                    password = "",
                ),
            dmlUserMetadata =
                DecryptedTenantMetadata.DmlUserMetadata(
                    host = "localhost",
                    port = "4000",
                    username = "root",
                    password = "",
                ),
        )
}
