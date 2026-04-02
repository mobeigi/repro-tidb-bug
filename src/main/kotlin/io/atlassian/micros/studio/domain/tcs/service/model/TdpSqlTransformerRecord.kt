package io.atlassian.micros.studio.domain.tcs.service.model

import java.util.UUID

data class TdpSqlTransformerRecord(
    val databaseName: String,
    val host: String,
    val port: Int,
    val credentialsUrl: String,
    val tenantId: UUID,
)
