package io.atlassian.micros.studio.application.spaces.repository.model

import io.atlassian.micros.studio.application.spaces.model.SpaceStatus
import io.atlassian.micros.studio.common.exposed.nativeEnum
import io.atlassian.micros.studio.common.model.exposed.BaseTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.javatime.datetime

object SpaceTable : BaseTable("space") {
    val id = javaUUID("id")
    val name = varchar("name", 200)
    val description = varchar("description", 500).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val createdBy = varchar("created_by", 128)
    val updatedBy = varchar("updated_by", 128).nullable()
    val status = nativeEnum<SpaceStatus>("status")
    val default = bool("is_default").default(false).index(customIndexName = "space_default_idx")

    override val primaryKey = PrimaryKey(arrayOf(workspaceId, id))

    init {
        index(
            customIndexName = "space_workspace_id_lower_name_id_idx",
            functions = listOf(workspaceId, name.lowerCase(), id),
        )
        index(customIndexName = "space_workspace_id_status_id_idx", columns = arrayOf(workspaceId, status, id))
        index(
            customIndexName = "space_workspace_id_status_lower_name_id_idx",
            functions = listOf(workspaceId, status, name.lowerCase(), id),
        )
    }
}
