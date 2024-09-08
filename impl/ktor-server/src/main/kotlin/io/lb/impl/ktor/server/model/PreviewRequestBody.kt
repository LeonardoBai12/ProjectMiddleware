package io.lb.impl.ktor.server.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class PreviewRequestBody(
    val originalResponse: JsonObject,
    val mappingRules: JsonObject
)
