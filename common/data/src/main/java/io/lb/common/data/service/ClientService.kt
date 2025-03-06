package io.lb.common.data.service

import io.lb.common.data.model.ApiValidationResponse
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import kotlinx.serialization.json.JsonObject

/**
 * Service for making requests to the middleware server.
 */
interface ClientService {
    /**
     * Makes a request to the middleware server.
     *
     * @param route The route to request.
     * @param preConfiguredQueries The pre-configured queries to include in the request.
     * @param preConfiguredHeaders The pre-configured headers to include in the request.
     * @param preConfiguredBody The pre-configured body to include in the request.
     *
     * @return The response from the middleware server.
     */
    suspend fun request(
        route: OriginalRoute,
        preConfiguredQueries: Map<String, String>,
        preConfiguredHeaders: Map<String, String>,
        preConfiguredBody: JsonObject?
    ): OriginalResponse

    /**
     * Validates an API by making a request to the base URL.
     *
     * @param api The API to validate.
     * @return The response from the API.
     */
    suspend fun validateApi(api: OriginalApi): ApiValidationResponse

    /**
     * Validates a user by making a request to the user service.
     *
     * @param secret The secret to use for validation.
     * @param audience The audience to use for validation.
     * @param issuer The issuer to use for validation.
     * @param userId The user ID to validate.
     * @param email The email of the user.
     * @param expiration The expiration time of the token.
     * @return Whether the user is valid.
     */
    suspend fun validateUser(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long,
    ): Boolean
}
