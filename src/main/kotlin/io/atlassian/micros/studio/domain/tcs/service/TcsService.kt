package io.atlassian.micros.studio.domain.tcs.service

import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.domain.tcs.service.model.TdpSqlTransformerRecord
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TcsService {
    fun getTdpSqlTransformerRecord(workspaceId: WorkspaceId): TdpSqlTransformerRecord =
        TdpSqlTransformerRecord(
            databaseName = "repro-db",
            host = "localhost",
            port = 4000,
            credentialsUrl = "http://localhost/fake",
            tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        )
}
