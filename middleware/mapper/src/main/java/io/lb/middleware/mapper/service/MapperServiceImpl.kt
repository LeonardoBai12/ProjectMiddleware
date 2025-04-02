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
import kotlinx.serialization.json.buildJsonArray
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

    override fun validateMappingRules(mappingRules: String): Boolean {
        return runCatching {
            json.decodeFromString<NewBodyMappingRule>(mappingRules)
            true
        }.getOrElse {
            false
        }
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
                message = "Failed to parse mapping rules."
            )
        }

        val originalJson = json.parseToJsonElement(originalResponse).jsonObject
        val parentElements = getParentElements(originalJson, rules)

        val resultArray = buildJsonArray {
            parentElements.forEach { parentElement ->
                val newJson = buildJsonObject {
                    for ((newKey, newField) in rules.newBodyFields) {
                        val oldField = rules.oldBodyFields[newKey]
                            ?: throw MiddlewareException(
                                code = MiddlewareStatusCode.BAD_REQUEST,
                                "Mapping rule for new key '$newKey' is missing in old body fields."
                            )

                        val value = extractValueFromParent(parentElement, oldField, rules.ignoreEmptyValues)
                        val transformedValue = transformValue(value, newField.type, rules.ignoreEmptyValues)

                        if (transformedValue !is JsonNull &&
                            (transformedValue is JsonPrimitive || transformedValue is JsonArray)
                        ) {
                            put(newField.key, transformedValue)
                        }
                    }
                }
                if (newJson.isNotEmpty()) {
                    add(newJson)
                }
            }
        }

        validateEmptyJson(resultArray)

        return if (resultArray.size == 1 && parentElements.size == 1) {
            json.encodeToString(resultArray.first())
        } else {
            json.encodeToString(resultArray)
        }
    }

    private fun getParentElements(
        originalJson: JsonObject,
        rules: NewBodyMappingRule
    ): List<JsonElement> {
        val allParentPaths = rules.oldBodyFields.values.map { it.parents }.distinct()

        return if (allParentPaths.isEmpty() || allParentPaths.all { it.isEmpty() }) {
            listOf(originalJson)
        } else {
            allParentPaths.flatMap { parentPath ->
                if (parentPath.isEmpty()) {
                    listOf(originalJson)
                } else {
                    findElementsByPath(originalJson, parentPath)
                }
            }.distinct()
        }
    }

    private fun findElementsByPath(
        jsonElement: JsonElement,
        path: List<String>
    ): List<JsonElement> {
        var currentElements: List<JsonElement> = listOf(jsonElement)

        for (key in path) {
            currentElements = currentElements.flatMap { element ->
                when (element) {
                    is JsonObject -> listOfNotNull(element[key])
                    is JsonArray -> element.mapNotNull { it.jsonObject[key] }
                    else -> emptyList()
                }
            }
        }

        return currentElements
    }

    private fun extractValueFromParent(
        parentElement: JsonElement,
        oldField: OldBodyField,
        ignoreEmptyValues: Boolean
    ): JsonElement? {
        val keys = oldField.keys

        return if (keys.size > 1) {
            var values = keys.map { key ->
                if (key.contains(",")) {
                    JsonPrimitive(concatenateValues(key, parentElement.jsonObject))
                } else {
                    parentElement.jsonObject[key] ?: JsonNull
                }
            }

            if (ignoreEmptyValues) {
                values = values.filter {
                    it !is JsonNull &&
                        it.jsonPrimitive.content.trim().isNotEmpty()
                }
            }

            JsonArray(values)
        } else {
            filterJsonElement(keys, parentElement, ignoreEmptyValues)
        }
    }

    private fun validateEmptyJson(jsonElement: JsonElement) {
        when (jsonElement) {
            is JsonArray -> if (jsonElement.isEmpty()) {
                throw MiddlewareException(
                    MiddlewareStatusCode.NOT_FOUND,
                    "All extracted values are empty."
                )
            }
            is JsonObject -> if (jsonElement.isEmpty()) {
                throw MiddlewareException(
                    MiddlewareStatusCode.NOT_FOUND,
                    "All extracted values are empty."
                )
            }
            else -> {
                throw MiddlewareException(
                    MiddlewareStatusCode.BAD_REQUEST,
                    "Invalid JSON element type."
                )
            }
        }
    }

    private fun filterJsonElement(
        keys: List<String>,
        currentElement: JsonElement?,
        ignoreEmptyValues: Boolean,
    ) = keys.fold(currentElement) { element, key ->
        if (element is JsonPrimitive) {
            return@fold element
        }
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
