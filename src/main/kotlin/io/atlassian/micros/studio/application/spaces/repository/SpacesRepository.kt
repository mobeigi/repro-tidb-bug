package io.atlassian.micros.studio.application.spaces.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.atlassian.micros.studio.application.database.exposed.ExposedConfig
import io.atlassian.micros.studio.application.spaces.exception.DefaultSpaceExistsException
import io.atlassian.micros.studio.application.spaces.model.Space
import io.atlassian.micros.studio.application.spaces.model.SpaceStatus
import io.atlassian.micros.studio.application.spaces.repository.exception.SpaceCreationFailedException
import io.atlassian.micros.studio.application.spaces.repository.extensions.toSpace
import io.atlassian.micros.studio.application.spaces.repository.model.GetSpacesCursor
import io.atlassian.micros.studio.application.spaces.repository.model.SpaceTable
import io.atlassian.micros.studio.common.SpaceId
import io.atlassian.micros.studio.common.WorkspaceId
import io.atlassian.micros.studio.common.model.Cursor
import io.atlassian.micros.studio.common.model.PaginatedResult
import io.atlassian.micros.studio.common.time.toLocalDateTimeUtc
import io.atlassian.micros.studio.domain.tcs.service.TcsService
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

/**
 * Repository for space operations.
 */
@Repository
class SpacesRepository(
    @param:Qualifier(ExposedConfig.DML_DATABASE) private val dmlDatabase: Database,
    private val tcsService: TcsService,
    private val objectMapper: ObjectMapper,
) {
    fun getSpaces(workspaceId: WorkspaceId): List<Space> =
        transaction(dmlDatabase) {
            SpaceTable
                .selectAll()
                .where { SpaceTable.workspaceId eq workspaceId }
                .map { it.toSpace() }
        }

    fun getSpaces(
        workspaceId: WorkspaceId,
        status: SpaceStatus? = null,
        query: String? = null,
        cursor: String? = null,
        limit: Int = 25,
    ): PaginatedResult<Space> {
        val getSpacesCursor = cursor?.let { Cursor.fromCursorString<GetSpacesCursor>(objectMapper, cursor) }

        System.err.println(getSpacesCursor)

        return transaction(dmlDatabase) {
            addLogger(StdOutSqlLogger)
            val exposedQuery =
                SpaceTable
                    .selectAll()
                    .where { SpaceTable.workspaceId eq workspaceId }
                    .apply {
                        if (getSpacesCursor != null) {
                            andWhere {
                                (SpaceTable.name.lowerCase() greater getSpacesCursor.spaceNameLower) or
                                    (
                                        (SpaceTable.name.lowerCase() eq getSpacesCursor.spaceNameLower) and
                                            (SpaceTable.id greater getSpacesCursor.spaceId)
                                    )
                            }
                        }

                        if (query != null) {
                            andWhere { SpaceTable.name.lowerCase() like "%${query.lowercase()}%" }
                        }

                        if (status != null) {
                            andWhere { SpaceTable.status eq status }
                        }
                    }.orderBy(SpaceTable.name.lowerCase() to SortOrder.ASC, SpaceTable.id to SortOrder.ASC)
                    .limit(limit + 1)

            val results = exposedQuery.map { it.toSpace() }
            val hasNext = results.size > limit
            val page = results.take(limit)
            val nextCursor =
                if (hasNext) {
                    val last = page.last()
                    GetSpacesCursor(last.name.lowercase(), last.spaceId).toCursorString(objectMapper)
                } else {
                    null
                }

            PaginatedResult(
                items = page,
                nextCursor = nextCursor,
                hasNext = hasNext,
            )
        }
    }

    fun getSpace(
        workspaceId: WorkspaceId,
        spaceId: SpaceId,
    ): Space? =
        transaction(dmlDatabase) {
            SpaceTable
                .selectAll()
                .where { (SpaceTable.workspaceId eq workspaceId) and (SpaceTable.id eq spaceId) }
                .singleOrNull()
                ?.toSpace()
        }

    fun getDefaultSpace(workspaceId: WorkspaceId): Space? =
        transaction(dmlDatabase) {
            val spaces =
                SpaceTable
                    .selectAll()
                    .where { (SpaceTable.workspaceId eq workspaceId) and (SpaceTable.default eq true) }
                    .limit(2)
                    .map { it.toSpace() }

            when (spaces.size) {
                0 -> null

                1 -> spaces.single()

                else -> throw DefaultSpaceExistsException(
                    "More than one default space exists for workspaceId $workspaceId",
                )
            }
        }

    fun createSpace(space: Space): Space {
        try {
            val tdpSqlTenantId = tcsService.getTdpSqlTransformerRecord(space.workspaceId).tenantId

            return transaction(dmlDatabase) {
                val insertResult =
                    SpaceTable.insert { row ->
                        row[tenantId] = tdpSqlTenantId.toString()
                        row[workspaceId] = space.workspaceId
                        row[id] = space.spaceId
                        row[name] = space.name
                        row[description] = space.description
                        row[createdAt] = space.createdAt.toLocalDateTimeUtc()
                        row[updatedAt] = space.updatedAt?.toLocalDateTimeUtc()
                        row[createdBy] = space.createdBy
                        row[updatedBy] = space.updatedBy
                        row[status] = space.status
                        row[default] = space.default
                    }
                val resultRow =
                    insertResult.resultedValues?.singleOrNull()
                        ?: throw SpaceCreationFailedException(
                            "Failed to create space for space id ${space.spaceId} in workspace ${space.workspaceId}",
                        )
                resultRow.toSpace()
            }
        } catch (e: ExposedSQLException) {
            throw SpaceCreationFailedException("Failed to create space for space id ${space.spaceId} in workspace ${space.workspaceId}", e)
        }
    }
}
