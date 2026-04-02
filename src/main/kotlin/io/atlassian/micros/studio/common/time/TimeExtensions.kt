package io.atlassian.micros.studio.common.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Instant.toLocalDateTimeUtc(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)

fun LocalDateTime.toInstantUtc(): Instant = this.toInstant(ZoneOffset.UTC)
