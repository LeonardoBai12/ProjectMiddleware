package io.lb.core.error

data class MiddlewareException(
    val code: Int,
    override val message: String?
) : Exception()
