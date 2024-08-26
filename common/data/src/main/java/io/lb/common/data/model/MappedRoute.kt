package io.lb.common.data.model

import io.lb.common.data.request.MiddlewareHttpMethods
import java.util.UUID

/**
 * Data class representing a mapped route.
 *
 * @property uuid The UUID of the mapped route.
 * @property path The path of the mapped route.
 * @property mappedApi The mapped API.
 * @property originalRoute The original route.
 * @property method The HTTP method of the mapped route.
 * @property rulesAsString The mapping rules as a string.
 * <br>Json example of the rules:
 * <br>[RulesExample.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/RulesExample.json)
 * <br>[ConcatenatedRulesExample.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/ConcatenatedRulesExample.json)
 */
data class MappedRoute(
    val uuid: UUID = UUID.randomUUID(),
    val path: String,
    val mappedApi: MappedApi,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val rulesAsString: String?
)
