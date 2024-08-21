package io.lb.middleware.mapper.model

import kotlinx.serialization.Serializable

/**
 * Represents a field in the old body.
 *
 * @property keys The keys of the field. There should be at least one key.
 * @property type The type of the field. It could be one on these Kotlin primitive types. \n
 * Types: \n
 * - String \n
 * - Int \n
 * - Double \n
 * - Boolean \n
 * @property parents The parent keys of the field. It will be empty if its a root field.
 */
@Serializable
data class OldBodyField(
    val keys: List<String>,
    val type: String,
    val parents: List<String> = emptyList()
)
