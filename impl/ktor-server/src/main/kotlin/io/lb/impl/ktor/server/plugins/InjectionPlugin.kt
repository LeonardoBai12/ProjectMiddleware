package io.lb.impl.ktor.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.lb.impl.ktor.server.model.TokenConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureInjection(modules: List<Module>) {
    install(Koin) {
        slf4jLogger()

        val appModule = module {
            single<CoroutineScope> {
                CoroutineScope(Dispatchers.Main)
            }
            single<TokenConfig> {
                TokenConfig.middlewareTokenConfig(
                    config = environment.config,
                    embedded = true
                )
            }
        }

        val mutableModules = modules.toMutableList()
        mutableModules.add(appModule)

        modules(mutableModules)
    }
}
