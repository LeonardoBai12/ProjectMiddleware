package io.lb.data.model

/**
 * Data class representing an original response.
 *
 * @property statusCode The status code of the original response.
 * @property body The body of the original response.
 */
data class OriginalResponse(
    val statusCode: Int,
    val body: String?
)
