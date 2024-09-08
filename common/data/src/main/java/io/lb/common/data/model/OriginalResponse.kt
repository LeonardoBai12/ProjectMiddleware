package io.lb.common.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing an original response.
 *
 * @property statusCode The status code of the original response.
 * @property body The body of the original response.
 */
@Serializable
data class OriginalResponse(
    val statusCode: Int,
    val body: String?
)
