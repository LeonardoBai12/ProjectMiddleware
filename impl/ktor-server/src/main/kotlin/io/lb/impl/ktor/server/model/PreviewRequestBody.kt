package io.lb.impl.ktor.server.model

import kotlinx.serialization.Serializable

@Serializable
data class PreviewRequestBody(
    val originalResponse: String,
    val mappingRules: String
)
