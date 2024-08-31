package io.lb.common.data.model

import java.util.UUID

/**
 * Data class representing a mapped API.
 *
 * @property uuid The UUID of the mapped API.
 * @property originalApi The original API.
 */
data class MappedApi(
    val uuid: UUID = UUID.randomUUID(),
    val originalApi: OriginalApi,
)
