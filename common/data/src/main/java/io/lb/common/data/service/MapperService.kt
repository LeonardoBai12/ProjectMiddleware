package io.lb.common.data.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.OriginalResponse
import io.lb.common.shared.error.MiddlewareException
import kotlin.jvm.Throws

/**
 * Service to map responses.
 * This service is responsible for mapping the original response to a new response based on the mapping rules.
 */
interface MapperService {
    /**
     * Maps the original response to a mapped response.
     *
     * @param mappingRules The mapping rules.
     * @param originalResponse The original response.
     *
     * @throws MiddlewareException If an error occurs during the mapping.
     *
     * @return The mapped response.
     */
    @Throws(MiddlewareException::class)
    fun mapResponse(
        mappingRules: String,
        originalResponse: OriginalResponse
    ): MappedResponse

    /**
     * Generates a preview of the JSON mapped response.
     *
     * @param mappingRules The mapping rules.
     * @param originalResponseBody The original response.
     *
     * @throws MiddlewareException If an error occurs during the mapping.
     *
     * @return The JSON preview.
     */
    @Throws(MiddlewareException::class)
    fun responseJsonPreview(
        mappingRules: String,
        originalResponseBody: String
    ): String

    /**
     * Validates the mapping rules.
     *
     * @param mappingRules The mapping rules.
     *
     * @return True if the mapping rules are valid, false otherwise.
     */
    fun validateMappingRules(mappingRules: String): Boolean
}
