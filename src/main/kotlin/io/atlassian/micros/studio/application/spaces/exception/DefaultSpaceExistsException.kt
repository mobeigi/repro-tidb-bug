package io.atlassian.micros.studio.application.spaces.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class DefaultSpaceExistsException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
