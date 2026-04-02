package io.atlassian.micros.studio.domain.tms.client.model

data class EvolveSchemaGroupStatus(
    val status: Status,
    val details: Details,
) {
    enum class Status {
        IN_PROGRESS,
        SUCCESS,
        FAILED_UNKNOWN_ERROR,
        FAILED_WITHOUT_DETAILS,
        FAILED_WITH_DETAILS,
        CANCELLED,
        TIMED_OUT,
    }

    data class Details(
        val total: Int,
        val inProgress: Int,
        val failed: Int,
        val success: Int,
        val percentageComplete: Int,
        val exceptionDetails: ExceptionDetails? = null,
    )

    data class ExceptionDetails(
        val databases: Map<String, DatabaseException>,
    )

    data class DatabaseException(
        val exceptionType: String? = null,
        val exceptionMessage: String? = null,
        val errorCode: Int? = null,
        val sqlState: String? = null,
        val fileName: String? = null,
        val lineNumber: Int? = null,
    )
}
