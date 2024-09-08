package io.lb.common.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Data class representing a mapped API.
 *
 * @property uuid The UUID of the mapped API.
 * @property originalApi The original API.
 */
@Serializable
data class MappedApi(
    val uuid: String = UUID.randomUUID().toString(),
    val originalApi: OriginalApi,
)
