package io.lb.middleware.mapper.model

import kotlinx.serialization.Serializable

/**
 * Represents a new body mapping rule.
 *
 * @param newBodyFields The new body fields.
 * @param oldBodyFields The old body fields.
 * @param ignoreEmptyValues Whether to ignore empty values.
*/
@Serializable
data class NewBodyMappingRule(
    val newBodyFields: Map<String, NewBodyField>,
    val oldBodyFields: Map<String, OldBodyField>,
    val ignoreEmptyValues: Boolean = false
)
