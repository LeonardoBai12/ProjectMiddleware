package io.lb.client.service

import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.lb.client.util.request
import io.lb.data.model.ApiValidationResponse
import io.lb.data.model.OriginalApi
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute
import io.lb.data.service.ClientService

/**
 * Implementation of the [ClientService] interface.
 *
 * @property client The HTTP client to make requests with.
 */
internal class ClientServiceImpl(
    private val client: HttpClient
) : ClientService {
    override suspend fun request(route: OriginalRoute): OriginalResponse {
        return client.request(route)
    }

    override suspend fun validateApi(api: OriginalApi): ApiValidationResponse {
        val response = client.head(api.baseUrl)
        return ApiValidationResponse(response.status.value)
    }
}
