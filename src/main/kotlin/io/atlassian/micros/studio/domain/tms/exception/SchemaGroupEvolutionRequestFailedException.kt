package io.atlassian.micros.studio.domain.tms.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class SchemaGroupEvolutionRequestFailedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
