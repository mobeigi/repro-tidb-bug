package io.atlassian.micros.studio.common.model

data class PaginatedResult<T>(
    val items: List<T>,
    val count: Int = items.size,
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)
