package io.lb.middleware.domain.controller

import io.lb.common.shared.flow.Resource
import io.lb.middleware.domain.model.MiddlewareEvent
import io.lb.middleware.domain.model.MiddlewareState
import io.lb.middleware.domain.usecases.ConfigureRoutesUseCase
import io.lb.middleware.domain.usecases.StartMiddlewareUseCase
import io.lb.middleware.domain.usecases.StopMiddlewareUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MiddlewareController(
    private val coroutineScope: CoroutineScope,
    private val startMiddlewareUseCase: StartMiddlewareUseCase,
    private val configureRoutesUseCase: ConfigureRoutesUseCase,
    private val stopMiddlewareUseCase: StopMiddlewareUseCase
) {
    private val _state = MutableStateFlow<MiddlewareState>(MiddlewareState.Idle)
    val state: StateFlow<MiddlewareState> = _state

    fun onEvent(event: MiddlewareEvent) {
        when (event) {
            MiddlewareEvent.StartMiddleware -> {
                startMiddleware()
            }
        }
    }

    private fun startMiddleware() {
        coroutineScope.launch {
            startMiddlewareUseCase().collectLatest {
                when (it) {
                    is Resource.Success -> configureStoredMappedRoutes()
                    is Resource.Error -> {
                        _state.value = MiddlewareState.Stopped
                        println("Error starting middleware: ${it.message}")
                        stopMiddlewareUseCase()
                    }
                }
            }
        }
    }

    private fun configureStoredMappedRoutes() {
        coroutineScope.launch {
            configureRoutesUseCase().collectLatest {
                when (it) {
                    is Resource.Success -> _state.value = MiddlewareState.Running
                    is Resource.Error -> {
                        _state.value = MiddlewareState.Stopped
                        println("Error configuring routes: ${it.message}")
                        stopMiddlewareUseCase()
                    }
                }
            }
        }
    }
}
