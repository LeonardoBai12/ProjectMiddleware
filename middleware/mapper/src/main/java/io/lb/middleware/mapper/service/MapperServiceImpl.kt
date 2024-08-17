package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.service.MapperService

class MapperServiceImpl : MapperService {
    override fun mapResponse(
        route: MappedRoute,
        originalResponse: OriginalResponse
    ): MappedResponse {
        TODO("Not yet implemented")
    }
}
