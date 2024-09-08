package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.service.MapperService
import io.lb.common.data.util.MiddlewareStatusCode
import io.lb.common.shared.error.MiddlewareException
import io.lb.middleware.mapper.model.NewBodyMappingRule
import io.lb.middleware.mapper.model.OldBodyField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Implementation of [MapperService].
 * This service is responsible for mapping the original response to a new response based on the mapping rules.
 */
internal class MapperServiceImpl : MapperService {
    private val json = Json { this.prettyPrint = true }

    override fun mapResponse(
        mappingRules: String,
        originalResponse: OriginalResponse,
    ): MappedResponse {
        return MappedResponse(
            statusCode = originalResponse.statusCode,
            body = mapOldResponse(mappingRules, originalResponse.body ?: "{}")
        )
    }

    override fun responseJsonPreview(
        mappingRules: String,
        originalResponseBody: String
    ): String {
        return mapOldResponse(mappingRules, originalResponseBody)
    }

    private fun mapOldResponse(
        mappingRules: String,
        originalResponse: String
    ): String {
        val rules = runCatching {
            json.decodeFromString<NewBodyMappingRule>(mappingRules)
        }.getOrElse {
            throw MiddlewareException(
                code = MiddlewareStatusCode.BAD_REQUEST,
                message = "Failed to parse mapping rules. Checkout our documentation: " +
                    "https://github.com/LeonardoBai12-Org/ProjectMiddleware"
            )
        }
        val originalJson = json.parseToJsonElement(originalResponse).jsonObject

        val newJson = buildJsonObject {
            for ((newKey, newField) in rules.newBodyFields) {
                val oldField = rules.oldBodyFields[newKey]
                    ?: throw MiddlewareException(
                        code = MiddlewareStatusCode.BAD_REQUEST,
                        "Mapping rule for new key '$newKey' is missing in old body fields."
                    )

                val value = extractValueFromOriginalJson(originalJson, oldField, rules.ignoreEmptyValues)
                val transformedValue = transformValue(value, newField.type, rules.ignoreEmptyValues)

                if (
                    transformedValue !is JsonNull &&
                    (transformedValue is JsonPrimitive || transformedValue is JsonArray)
                ) {
                    put(newField.key, transformedValue)
                }
            }
        }

        validateEmptyJson(newJson)

        return json.encodeToString(newJson)
    }

    private fun validateEmptyJson(newJson: JsonObject) {
        if (newJson.isEmpty()) {
            throw MiddlewareException(
                MiddlewareStatusCode.NOT_FOUND,
                "All extracted values are empty."
            )
        }
    }

    private fun extractValueFromOriginalJson(
        originalResponse: JsonObject,
        oldField: OldBodyField,
        ignoreEmptyValues: Boolean
    ): JsonElement? {
        val keys = oldField.keys
        val parentKeys = oldField.parents
        var currentElement: JsonElement? = originalResponse

        if (parentKeys.isNotEmpty()) {
            for (key in parentKeys) {
                currentElement = currentElement?.jsonObject?.get(key)?.jsonArray?.first()
                    ?: throw MiddlewareException(
                        code = MiddlewareStatusCode.BAD_REQUEST,
                        "Parent key '$key' not found in original JSON."
                    )
            }
        } else {
            currentElement = currentElement?.jsonObject?.get(keys[0])?.jsonArray?.first()
                ?: throw MiddlewareException(
                    code = MiddlewareStatusCode.BAD_REQUEST,
                    "Key '${keys[0]}' not found in original JSON root."
                )
        }

        return runCatching {
            if (keys.size > 1) {
                var values = keys.map { key ->
                    if (key.contains(",")) {
                        return@map JsonPrimitive(concatenateValues(key, currentElement?.jsonObject))
                    }
                    currentElement?.jsonObject?.get(key) ?: JsonNull
                }

                if (ignoreEmptyValues) {
                    values = values.filter {
                        it !is JsonNull &&
                            it.jsonPrimitive.content.trim().isNotEmpty()
                    }
                }

                return JsonArray(
                    values
                )
            }

            filterJsonElement(keys, currentElement, ignoreEmptyValues)
        }.getOrElse { JsonNull }
    }

    private fun filterJsonElement(
        keys: List<String>,
        currentElement: JsonElement?,
        ignoreEmptyValues: Boolean,
    ) = keys.fold(currentElement) { element, key ->
        with(element?.jsonObject?.get(key)) {
            takeIf {
                ignoreEmptyValues && (
                    it is JsonNull ||
                        it?.jsonPrimitive?.content?.trim().isNullOrEmpty()
                    )
            }?.let {
                JsonNull
            } ?: this
        }
    }

    private fun concatenateValues(
        key: String,
        jsonObject: JsonObject?
    ): String {
        var finalValue = ""

        key.split(",").map {
            it.trim()
        }.forEach {
            if (jsonObject?.get(it) !is JsonNull) {
                finalValue += " " + jsonObject?.get(it)?.jsonPrimitive?.content?.trim().orEmpty()
            }
        }

        return finalValue.trim()
    }

    private fun transformValue(
        value: JsonElement?,
        type: String,
        ignoreEmptyValues: Boolean
    ): JsonElement {
        if (value is JsonArray) {
            return JsonArray(
                value.filter {
                    it !is JsonNull
                }.map {
                    transformValue(it, type, ignoreEmptyValues)
                }
            )
        }

        return when (type) {
            "String" -> JsonPrimitive(value?.jsonPrimitive?.content?.trim().orEmpty())
            "Int" -> JsonPrimitive(value?.jsonPrimitive?.content?.toIntOrNull())
            "Double" -> JsonPrimitive(value?.jsonPrimitive?.content?.toDoubleOrNull())
            "Boolean" -> JsonPrimitive(value?.jsonPrimitive?.content?.toBoolean())
            else -> JsonNull
        }
    }
}
