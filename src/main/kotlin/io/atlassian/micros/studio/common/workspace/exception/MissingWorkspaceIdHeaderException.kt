package io.atlassian.micros.studio.common.workspace.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class MissingWorkspaceIdHeaderException : RuntimeException()
