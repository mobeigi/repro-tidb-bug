package io.atlassian.micros.studio.common.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Syntax sugar to get a logger instance without specifying the current class.
 */
inline fun <reified T> T.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
