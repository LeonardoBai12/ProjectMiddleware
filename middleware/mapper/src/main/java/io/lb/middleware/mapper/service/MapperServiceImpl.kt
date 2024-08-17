package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.service.MapperService
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

class MapperServiceImpl : MapperService {
    override fun mapResponse(
        route: MappedRoute,
        originalResponse: OriginalResponse,
    ): MappedResponse {
        return MappedResponse(
            statusCode = originalResponse.statusCode,
            body = mapOldResponse(route, originalResponse)
        )
    }

    override fun responseJsonPreview(
        route: MappedRoute,
        originalResponse: OriginalResponse
    ): String {
        return mapOldResponse(route, originalResponse)
    }

    private fun mapOldResponse(
        route: MappedRoute,
        originalResponse: OriginalResponse
    ): String {
        val rules = Json.decodeFromString<NewBodyMappingRule>(route.rulesAsString.orEmpty())
        val originalJson = Json.parseToJsonElement(originalResponse.body ?: "{}").jsonObject

        val newJson = buildJsonObject {
            for ((newKey, newField) in rules.newBodyFields) {
                val oldField = rules.oldBodyFields[newKey] ?: continue

                val value = extractValueFromOriginalJson(originalJson, oldField, rules.ignoreEmptyValues)
                val transformedValue = transformValue(value, newField.type, rules.ignoreEmptyValues)

                if (transformedValue is JsonPrimitive && transformedValue.jsonPrimitive.content != "null") {
                    put(newField.key, transformedValue)
                } else if (transformedValue is JsonArray) {
                    put(newField.key, transformedValue)
                }
            }
        }

        return Json.encodeToString(newJson)
    }

    private fun extractValueFromOriginalJson(
        jsonObject: JsonObject,
        oldField: OldBodyField,
        ignoreEmptyValues: Boolean
    ): JsonElement? {
        val keys = oldField.keys
        val parentKeys = oldField.parents

        var currentElement: JsonElement? = jsonObject

        for (key in parentKeys) {
            currentElement = currentElement?.jsonObject?.get(key)?.jsonArray?.first()
        }

        return runCatching {
            if (keys.size > 1) {
                var values = keys.map { key ->
                    currentElement?.jsonObject?.get(key) ?: JsonNull
                }

                if (ignoreEmptyValues) {
                    values = values.filter {
                        it.jsonPrimitive.content.trim().isNotEmpty()
                    }
                }

                return JsonArray(values)
            }

            keys.fold(currentElement) { element, key ->
                with(element?.jsonObject?.get(key)) {
                    takeIf {
                        ignoreEmptyValues && it?.jsonPrimitive?.content?.trim().isNullOrEmpty()
                    }?.let {
                        JsonNull
                    } ?: this
                }
            }
        }.getOrElse { JsonNull }
    }

    private fun transformValue(
        value: JsonElement?,
        type: String,
        ignoreEmptyValues: Boolean
    ): JsonElement {
        if (value is JsonArray) {
            return JsonArray(value.map { transformValue(it, type, ignoreEmptyValues) })
        }

        return when (type) {
            "String" -> JsonPrimitive(value?.jsonPrimitive?.content.orEmpty())
            "Int" -> JsonPrimitive(value?.jsonPrimitive?.content?.toIntOrNull() ?: 0)
            "Double" -> JsonPrimitive(value?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0)
            "Boolean" -> JsonPrimitive(value?.jsonPrimitive?.content?.toBoolean() ?: false)
            else -> JsonNull
        }
    }
}
