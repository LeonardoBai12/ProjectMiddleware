package io.lb.impl.ktor.client.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.lb.common.data.model.ApiValidationResponse
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.service.ClientService
import io.lb.impl.ktor.client.ClientConstants
import io.lb.impl.ktor.client.model.UserData
import io.lb.impl.ktor.client.util.request
import kotlinx.serialization.json.JsonObject
import java.util.Date

/**
 * Implementation of the [ClientService] interface.
 *
 * @property client The HTTP client to make requests with.
 */
internal class ClientServiceImpl(
    private val client: HttpClient
) : ClientService {
    override suspend fun request(
        route: OriginalRoute,
        preConfiguredQueries: Map<String, String>,
        preConfiguredHeaders: Map<String, String>,
        preConfiguredBody: JsonObject?
    ): OriginalResponse {
        return client.request(
            originalRoute = route,
            preConfiguredQueries = preConfiguredQueries,
            preConfiguredHeaders = preConfiguredHeaders,
            preConfiguredBody = preConfiguredBody
        )
    }

    override suspend fun validateApi(api: OriginalApi): ApiValidationResponse {
        val response = client.head(api.baseUrl)
        return ApiValidationResponse(response.status.value)
    }

    override suspend fun validateUser(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long,
    ): Boolean {
        val result = client.get {
            url("${ClientConstants.USER_SERVICE_URL}/user?userId=$userId")
            bearerAuth(generateToken(secret, audience, issuer, userId, email, expiration))
        }
        val body = result.body<UserData?>()
        return result.status == HttpStatusCode.OK && body?.userId == userId && body.email == email
    }

    /**
     * Generates a JWT Bearer Token for the new user session.
     *
     * @param secret The secret to use for validation.
     * @param audience The audience to use for validation.
     * @param issuer The issuer to use for validation.
     * @param userId The user ID to validate.
     * @param email The email of the user.
     * @param expiration The expiration time of the token.
     *
     * @return The generated token.
     */
    private fun generateToken(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long,
    ): String {
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(expiration))
            .withClaim("userId", userId)
            .withClaim("email", email)
        return token.sign(Algorithm.HMAC256(secret))
    }
}
