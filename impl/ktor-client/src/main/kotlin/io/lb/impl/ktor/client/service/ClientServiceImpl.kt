package io.lb.impl.ktor.client.service

import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.lb.common.data.model.ApiValidationResponse
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.service.ClientService
import io.lb.impl.ktor.client.util.request
import kotlinx.serialization.json.JsonObject

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
}
