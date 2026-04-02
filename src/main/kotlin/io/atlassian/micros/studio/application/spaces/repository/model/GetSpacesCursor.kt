package io.atlassian.micros.studio.application.spaces.repository.model

import io.atlassian.micros.studio.common.SpaceId
import io.atlassian.micros.studio.common.model.Cursor

data class GetSpacesCursor(
    val spaceNameLower: String,
    val spaceId: SpaceId,
) : Cursor
