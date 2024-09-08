package io.lb.middleware.mapper.di

import io.lb.common.data.service.MapperService
import io.lb.middleware.mapper.service.MapperServiceImpl

fun provideMapperService(): MapperService = MapperServiceImpl()
