package io.lb.common.shared.error

/**
 * Exception thrown by middleware.
 *
 * @property code The error code.
 * @property message The error message.
 */
data class MiddlewareException(
    val code: Int,
    override val message: String?
) : Exception()
