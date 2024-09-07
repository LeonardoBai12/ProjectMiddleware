package io.lb.middleware.mapper.model

import kotlinx.serialization.Serializable

/**
 * Represents a field in the new body.
 *
 * @property key The key of the field.
 * @property type The type of the field. It could be any Kotlin primitive type.
 * <br>Allowed types:
 * <br>- [String]
 * <br>- [Int]
 * <br>- [Double]
 * <br>- [Boolean]
 */
@Serializable
internal data class NewBodyField(
    val key: String,
    val type: String
)
