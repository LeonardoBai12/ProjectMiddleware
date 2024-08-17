package io.lb.common.data.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse

/**
 * Service to map responses.
 */
interface MapperService {
    /**
     * Maps the original response to a mapped response.
     *
     * @param route The route.
     * @param originalResponse The original response.
     * @return The mapped response.
     */
    fun mapResponse(
        route: MappedRoute,
        originalResponse: OriginalResponse
    ): MappedResponse
}
