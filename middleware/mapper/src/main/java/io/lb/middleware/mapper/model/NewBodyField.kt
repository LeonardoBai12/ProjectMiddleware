package io.lb.middleware.mapper.model

import kotlinx.serialization.Serializable

/**
 * Represents a field in the new body.
 *
 * @property key The key of the field.
 * @property type The type of the field. It could be any Kotlin primitive type. \n
 * Types: \n
 * - String \n
 * - Int \n
 * - Long \n
 * - Float \n
 */
@Serializable
data class NewBodyField(
    val key: String,
    val type: String
)
