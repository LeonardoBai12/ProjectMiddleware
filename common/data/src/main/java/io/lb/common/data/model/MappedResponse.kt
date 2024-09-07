package io.lb.common.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a mapped response.
 *
 * @property statusCode The status code of the mapped response.
 * @property body The body of the mapped response.
 */
@Serializable
data class MappedResponse(
    val statusCode: Int,
    val body: String?
)
