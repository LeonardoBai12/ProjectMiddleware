package io.lb.impl.ktor.server.model

import io.lb.common.data.model.MappedApi
import kotlinx.serialization.Serializable

@Serializable
data class MappedApiParameter(
    val originalApi: OriginalApiParameter
) {
    fun toMappedApi() = MappedApi(
        originalApi = originalApi.toOriginalApi()
    )
}
