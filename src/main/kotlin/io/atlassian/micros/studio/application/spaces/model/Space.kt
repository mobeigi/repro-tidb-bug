package io.atlassian.micros.studio.application.spaces.model

import io.atlassian.micros.studio.common.SpaceId
import io.atlassian.micros.studio.common.WorkspaceId
import java.time.Instant

data class Space(
    val workspaceId: WorkspaceId,
    val spaceId: SpaceId,
    val name: String,
    val description: String?,
    val createdBy: String,
    val updatedBy: String?,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val status: SpaceStatus,
    val default: Boolean,
)
