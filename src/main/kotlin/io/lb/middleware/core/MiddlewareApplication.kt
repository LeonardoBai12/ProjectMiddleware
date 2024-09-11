package io.lb.middleware.core

import io.lb.middleware.core.di.configureServer
import io.lb.middleware.core.di.provideMiddleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun main() {
    val engine = configureServer()
    val middleware = provideMiddleware(CoroutineScope(Dispatchers.Default), engine)
    middleware.start()
    engine.start(wait = true)
}
