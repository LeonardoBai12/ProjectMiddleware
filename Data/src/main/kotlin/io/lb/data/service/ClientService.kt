package io.lb.data.service

import io.lb.data.model.ApiValidationResponse
import io.lb.data.model.OriginalApi
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute

interface ClientService {
    suspend fun request(route: OriginalRoute): OriginalResponse
    suspend fun validateApi(api: OriginalApi): ApiValidationResponse
}
