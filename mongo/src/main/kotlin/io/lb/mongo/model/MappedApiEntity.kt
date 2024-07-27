package io.lb.mongo.model

import io.lb.data.model.MappedApi
import io.lb.data.model.OriginalApi
import java.util.UUID

data class MappedApiEntity(
    val uuid: UUID,
    val originalApi: OriginalApi,
    val name: String,
    val routes: List<MappedRouteEntity> = listOf()
) {
    internal fun toMappedApi(): MappedApi {
        return MappedApi(
            uuid = this.uuid,
            originalApi = this.originalApi,
            name = this.name,
        )
    }
}
