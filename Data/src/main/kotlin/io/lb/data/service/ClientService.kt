package io.lb.data.service

import io.lb.data.model.ApiValidationResponse
import io.lb.data.model.OriginalApi
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute

/**
 * Service for making requests to the middleware server.
 */
interface ClientService {
    /**
     * Makes a request to the middleware server.
     *
     * @param route The route to request.
     * @param queries The queries to include in the request.
     * @return The response from the middleware server.
     */
    suspend fun request(route: OriginalRoute, queries: Map<String, String>): OriginalResponse

    /**
     * Validates an API by making a request to the base URL.
     *
     * @param api The API to validate.
     * @return The response from the API.
     */
    suspend fun validateApi(api: OriginalApi): ApiValidationResponse
}
