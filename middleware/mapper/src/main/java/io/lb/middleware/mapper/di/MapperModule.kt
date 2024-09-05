package io.lb.middleware.mapper.di

import io.lb.common.data.service.MapperService
import io.lb.middleware.mapper.service.MapperServiceImpl
import org.koin.dsl.module

val mapperModule = module {
    single<MapperService> { MapperServiceImpl() }
}
