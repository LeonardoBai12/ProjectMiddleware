package io.lb.common.data.model

import kotlinx.serialization.Serializable

/**
 * Data class containing the status code of an API validation response.
 *
 * @property statusCode The status code of the API validation response.
 */
@Serializable
data class ApiValidationResponse(
    val statusCode: Int
)
