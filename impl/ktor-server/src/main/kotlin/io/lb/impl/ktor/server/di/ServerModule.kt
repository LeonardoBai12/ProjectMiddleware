package io.lb.impl.ktor.server.di

import io.ktor.server.netty.NettyApplicationEngine
import io.lb.common.data.service.ServerService
import io.lb.impl.ktor.server.service.ServerServiceImpl

fun provideServerService(
    engine: NettyApplicationEngine
): ServerService = ServerServiceImpl(engine)
