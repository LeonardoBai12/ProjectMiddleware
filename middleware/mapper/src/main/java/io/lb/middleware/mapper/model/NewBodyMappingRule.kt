package io.lb.middleware.mapper.model

import kotlinx.serialization.Serializable

/**
 * Represents a new body mapping rule.
 *
 * - Json example of the rules:
 * [RulesExample.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/NewRouteExample.json)
 *
 * - Json example of a response provided by [TheMealDB](https://www.themealdb.com/api.php).
 * [ExampleBefore.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/ExampleBefore.json)
 *
 * - Json example of a mapped response using the rules from the example above:
 * [ExampleAfter.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/ExampleAfter.json)
 *
 * - Json example of a mapped response using the rules from the example above (with empty values):
 * [ExampleAfter.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/ExampleAfterWithEmptyValues.json)
 *
 * @property newBodyFields The new body fields.
 * @property oldBodyFields The old body fields.
 * @property ignoreEmptyValues Whether to ignore empty values.
*/
@Serializable
data class NewBodyMappingRule(
    val newBodyFields: Map<String, NewBodyField>,
    val oldBodyFields: Map<String, OldBodyField>,
    val ignoreEmptyValues: Boolean = false
)
