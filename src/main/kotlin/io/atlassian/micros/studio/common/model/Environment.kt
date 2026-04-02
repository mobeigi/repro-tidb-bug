package io.atlassian.micros.studio.common.model

enum class Environment(
    val value: String,
) {
    LOCAL("local"),
    DEVELOPMENT("dev"),
    STAGING("stg"),
    PRODUCTION("prod"),
}
