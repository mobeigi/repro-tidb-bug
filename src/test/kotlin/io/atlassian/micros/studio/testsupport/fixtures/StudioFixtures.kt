package io.atlassian.micros.studio.testsupport.fixtures

import io.atlassian.micros.studio.application.spaces.model.SpaceStatus
import io.atlassian.micros.studio.common.SpaceId
import io.atlassian.micros.studio.common.WorkspaceId
import java.time.Instant
import java.util.UUID

object StudioFixtures {
    object Workspace {
        fun randomId(): WorkspaceId = UUID.randomUUID()
    }

    object Space {
        fun randomId(): SpaceId = UUID.randomUUID()

        fun random(status: SpaceStatus): io.atlassian.micros.studio.application.spaces.model.Space =
            io.atlassian.micros.studio.application.spaces.model.Space(
                workspaceId = Workspace.randomId(),
                spaceId = randomId(),
                name = "Example Space (${StringFixtures.generateRandomAlphanumericString(5)})",
                description = "Example Description (${StringFixtures.generateRandomAlphanumericString(5)})",
                createdBy = IdentityFixtures.AtlassianAccount.randomId(),
                updatedBy = null,
                createdAt = Instant.now(),
                updatedAt = null,
                status = status,
                default = false,
            )

        fun defaultSpace(): io.atlassian.micros.studio.application.spaces.model.Space =
            io.atlassian.micros.studio.application.spaces.model.Space(
                workspaceId = Workspace.randomId(),
                spaceId = randomId(),
                name = "Default Space (${StringFixtures.generateRandomAlphanumericString(5)})",
                description = "Default space description (${StringFixtures.generateRandomAlphanumericString(5)})",
                createdBy = IdentityFixtures.AtlassianAccount.randomId(),
                updatedBy = null,
                createdAt = Instant.now(),
                updatedAt = null,
                status = SpaceStatus.CURRENT,
                default = true,
            )
    }
}
