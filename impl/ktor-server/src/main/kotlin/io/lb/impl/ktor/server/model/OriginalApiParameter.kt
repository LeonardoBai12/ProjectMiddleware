package io.lb.impl.ktor.server.model

import io.lb.common.data.model.OriginalApi
import kotlinx.serialization.Serializable

@Serializable
internal data class OriginalApiParameter(
    val baseUrl: String
) {
    fun toOriginalApi() = OriginalApi(
        baseUrl = baseUrl
    )
}
