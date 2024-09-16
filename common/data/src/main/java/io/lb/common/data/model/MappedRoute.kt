package io.lb.common.data.model

import io.lb.common.data.request.MiddlewareHttpMethods
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.UUID

/**
 * Data class representing a mapped route.
 *
 * @property uuid The UUID of the mapped route.
 * @property path The path of the mapped route.
 * @property mappedApi The mapped API.
 * @property originalRoute The original route.
 * @property method The HTTP method of the mapped route.
 * @property preConfiguredQueries The pre-configured queries of the mapped route.
 * @property preConfiguredHeaders The pre-configured headers of the mapped route.
 * @property preConfiguredBody The pre-configured body of the mapped route.
 * @property rulesAsString The mapping rules as a string.
 * <br>Json example of the rules:
 * <br>[RulesExample.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/RulesExample.json)
 * <br>[ConcatenatedRulesExample.json](https://github.com/LeonardoBai12-Org/ProjectMiddleware/blob/main/JsonExamples/ConcatenatedRulesExample.json)
 */
@Serializable
data class MappedRoute(
    val uuid: String = UUID.randomUUID().toString(),
    val path: String,
    val mappedApi: MappedApi,
    var originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val preConfiguredQueries: Map<String, String> = mapOf(),
    val preConfiguredHeaders: Map<String, String> = originalRoute.headers,
    val preConfiguredBody: JsonObject? = originalRoute.body,
    val rulesAsString: String?
)
