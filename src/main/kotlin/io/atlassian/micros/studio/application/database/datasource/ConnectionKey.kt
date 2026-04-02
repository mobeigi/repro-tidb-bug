package io.atlassian.micros.studio.application.database.datasource

data class ConnectionKey(
    val host: String,
    val port: String,
    val username: String,
    val password: String,
)
