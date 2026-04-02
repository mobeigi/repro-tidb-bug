package io.atlassian.micros.studio.domain.tms.client.model

data class SchemaEvolutionHealthStatus(
    val isHealthy: Boolean,
    val message: String,
)
