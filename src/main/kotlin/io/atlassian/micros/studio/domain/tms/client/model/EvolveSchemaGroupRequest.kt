package io.atlassian.micros.studio.domain.tms.client.model

data class EvolveSchemaGroupRequest(
    val groupName: String,
    val region: String,
    val migrationArtifactRequest: MigrationArtifactRequest,
    val executionId: String,
    val failFast: Boolean,
) {
    data class MigrationArtifactRequest(
        val type: String,
        val spec: Spec,
    ) {
        data class Spec(
            val image: String,
        )
    }
}
