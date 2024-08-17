package io.lb.impl.mongo.database.model

import io.lb.data.model.MappedApi
import io.lb.data.model.OriginalApi
import java.util.UUID

/**
 * Data class representing a mapped API entity.
 *
 * @param uuid UUID of the mapped API.
 * @param originalApi Original API of the mapped API.
 * @param name Name of the mapped API.
 * @param routes List of routes of the mapped API.
 */
data class MappedApiEntity(
    val uuid: UUID,
    val originalApi: OriginalApi,
    val name: String,
    val routes: List<MappedRouteEntity> = listOf()
) {
    /**
     * Converts the MappedApiEntity to a MappedApi.
     *
     * @return A MappedApi instance.
     */
    internal fun toMappedApi(): MappedApi {
        return MappedApi(
            uuid = this.uuid,
            originalApi = this.originalApi,
            name = this.name,
        )
    }
}
