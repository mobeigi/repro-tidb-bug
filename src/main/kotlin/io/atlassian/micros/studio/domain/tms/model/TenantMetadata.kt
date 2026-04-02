package io.atlassian.micros.studio.domain.tms.model

data class TenantMetadata(
    val status: String,
    val databaseName: String,
    val ddlUserMetadata: DdlUserMetadata,
    val dmlUserMetadata: DmlUserMetadata,
) {
    data class DdlUserMetadata(
        val host: String,
        val port: String,
        val username: String,
        val password: String,
    )

    data class DmlUserMetadata(
        val host: String,
        val port: String,
        val username: String,
        val password: String,
    )
}
