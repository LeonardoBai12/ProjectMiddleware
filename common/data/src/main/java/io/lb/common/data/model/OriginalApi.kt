package io.lb.common.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing an original API.
 *
 * @property baseUrl The base URL of the original API.
 */
@Serializable
data class OriginalApi(
    val baseUrl: String
)
