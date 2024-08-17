package io.lb.mapper.service

import io.lb.data.model.MappedResponse
import io.lb.data.model.MappedRoute
import io.lb.data.model.OriginalResponse
import io.lb.data.service.MapperService

class MapperServiceImpl : MapperService {
    override fun mapResponse(
        route: MappedRoute,
        originalResponse: OriginalResponse
    ): MappedResponse {
        TODO("Not yet implemented")
    }
}
