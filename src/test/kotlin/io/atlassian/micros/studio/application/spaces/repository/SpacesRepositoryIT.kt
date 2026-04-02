package io.atlassian.micros.studio.application.spaces.repository

import io.atlassian.micros.studio.application.database.exposed.ExposedConfig
import io.atlassian.micros.studio.application.spaces.model.SpaceStatus
import io.atlassian.micros.studio.application.spaces.repository.model.SpaceTable
import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.testsupport.IntegrationTest
import io.atlassian.micros.studio.testsupport.fixtures.StudioFixtures
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

@IntegrationTest
class SpacesRepositoryIT {
    @Autowired
    private lateinit var spacesRepository: SpacesRepository

    @Autowired
    @Qualifier(ExposedConfig.DDL_DATABASE)
    private lateinit var ddlDatabase: Database

    @BeforeAll
    fun setUp() {
        transaction(ddlDatabase) {
            SchemaUtils.drop(SpaceTable)
            SchemaUtils.create(SpaceTable)
        }
    }

    @Nested
    inner class GetSpacesIT {
        @Nested
        inner class CombinedFiltersIT {
            private lateinit var workspaceId: WorkspaceId

            @BeforeEach
            fun setUpCombinedFilters() {
                workspaceId = StudioFixtures.Workspace.randomId()
            }

            //            @Test
            @RepeatedTest(50)
            fun shouldCombineAllThreeFiltersSearchStatusAndPagination() {
                System.err.println("workspaceId 1: $workspaceId")

                // Create 5 "Backend" spaces with CURRENT status
                repeat(5) { index ->
                    val space =
                        StudioFixtures.Space.random(SpaceStatus.CURRENT).copy(
                            workspaceId = workspaceId,
                            name = "Backend Team $index",
                        )
                    spacesRepository.createSpace(space)
                }

                // Create 3 "Backend" spaces with ARCHIVED status
                repeat(3) { index ->
                    val space =
                        StudioFixtures.Space.random(SpaceStatus.ARCHIVED).copy(
                            workspaceId = workspaceId,
                            name = "Backend Archive $index",
                        )
                    spacesRepository.createSpace(space)
                }

                // Create 2 "Frontend" spaces with CURRENT status
                repeat(2) { index ->
                    val space =
                        StudioFixtures.Space.random(SpaceStatus.CURRENT).copy(
                            workspaceId = workspaceId,
                            name = "Frontend Team $index",
                        )
                    spacesRepository.createSpace(space)
                }

                System.err.println("workspaceId 2: $workspaceId")

                // TODO check
                val spaces = spacesRepository.getSpaces(workspaceId, limit = 100)
                System.err.println("all spaces: $spaces")

                // When - Search for "Backend" with CURRENT status and pagination limit 2
                val firstPage =
                    spacesRepository.getSpaces(
                        workspaceId,
                        cursor = null,
                        limit = 2,
                        query = "Backend",
                        status = SpaceStatus.CURRENT,
                    )

                System.err.println("workspaceId 3: $workspaceId")
                System.err.println("firstPage: $firstPage")

                // Then - Should get first 2 of the 5 "Backend" CURRENT spaces
                assertThat(firstPage.items).hasSize(2)
                assertThat(firstPage.hasNext).isTrue()

                System.err.println("workspaceId 4: $workspaceId")

                // When - Get next page
                val secondPage =
                    spacesRepository.getSpaces(
                        workspaceId,
                        cursor = firstPage.nextCursor,
                        limit = 2,
                        query = "Backend",
                        status = SpaceStatus.CURRENT,
                    )

                System.err.println("workspaceId 5: $workspaceId")
                System.err.println("secondPage: $secondPage")

                // Then - Should get next 2 spaces
                assertThat(secondPage.items).hasSize(2)
                assertThat(secondPage.hasNext).isTrue()

                // When - Get final page
                val thirdPage =
                    spacesRepository.getSpaces(
                        workspaceId,
                        cursor = secondPage.nextCursor,
                        limit = 2,
                        query = "Backend",
                        status = SpaceStatus.CURRENT,
                    )

                // Then - Should get final space
                assertThat(thirdPage.items).hasSize(1)
                assertThat(thirdPage.hasNext).isFalse()
            }
        }
    }
}
