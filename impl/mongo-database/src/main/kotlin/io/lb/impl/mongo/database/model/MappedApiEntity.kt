package io.lb.impl.mongo.database.model

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.OriginalApi

/**
 * Data class representing a mapped API entity.
 *
 * @property uuid UUID of the mapped API.
 * @property originalApi Original API of the mapped API.
 * @property routes List of routes of the mapped API.
 */
internal data class MappedApiEntity(
    val uuid: String,
    val originalApi: OriginalApi,
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
        )
    }
}
