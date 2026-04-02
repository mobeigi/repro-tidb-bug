package io.atlassian.micros.studio.application.spaces.repository.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class SpaceCreationFailedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
