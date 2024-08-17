package io.lb.data.model

/**
 * Data class representing a mapped response.
 *
 * @property statusCode The status code of the mapped response.
 * @property body The body of the mapped response.
 */
data class MappedResponse(
    val statusCode: Int,
    val body: String?
)
