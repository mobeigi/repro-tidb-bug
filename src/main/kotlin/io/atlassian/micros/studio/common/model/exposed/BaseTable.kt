package io.atlassian.micros.studio.common.model.exposed

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID

/**
 * All tables should extend this base table.
 */
abstract class BaseTable(
    name: String = "",
) : Table(name) {
    val workspaceId = javaUUID("workspace_id")

    // All tables must have a column that demarcates tenant boundaries
    // The field name must match the tenantSeparationColumn in the titan-config
    // This column must contain the tenantId from the tdp-sql transformer record in TCS for the given workspaceId
    // Also see: https://developer.atlassian.com/platform/tdpsql/reference/schema-evolution-guidelines/#tdp-sql-schema-restrictions
    val tenantId = varchar("tenant_id", 100).index(customIndexName = "${name}_tenant_id_idx")
}
