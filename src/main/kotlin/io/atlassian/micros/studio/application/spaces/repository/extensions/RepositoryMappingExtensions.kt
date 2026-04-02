package io.atlassian.micros.studio.application.spaces.repository.extensions

import io.atlassian.micros.studio.application.spaces.model.Space
import io.atlassian.micros.studio.application.spaces.repository.model.SpaceTable
import io.atlassian.micros.studio.common.time.toInstantUtc
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toSpace(): Space =
    Space(
        workspaceId = this[SpaceTable.workspaceId],
        spaceId = this[SpaceTable.id],
        name = this[SpaceTable.name],
        description = this[SpaceTable.description],
        createdAt = this[SpaceTable.createdAt].toInstantUtc(),
        updatedAt = this[SpaceTable.updatedAt]?.toInstantUtc(),
        createdBy = this[SpaceTable.createdBy],
        updatedBy = this[SpaceTable.updatedBy],
        status = this[SpaceTable.status],
        default = this[SpaceTable.default],
    )
