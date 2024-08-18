package io.lb.common.data.request

/**
 * Data class representing a middleware auth header.
 *
 * @property type The type of the middleware auth header.
 * @property token The token of the middleware auth header.
 */
data class MiddlewareAuthHeader(
    val type: MiddlewareAuthHeaderType,
    val token: String
) {
    fun fullToken(): String {
        if (type == MiddlewareAuthHeaderType.None) {
            return token
        }

        return "${type.name} $token"
    }
}

/**
 * Enum class representing a middleware auth header type.
 *
 * @property None No middleware auth header type.
 * @property Basic Basic middleware auth header type.
 * @property Bearer Bearer middleware auth header type.
 */
enum class MiddlewareAuthHeaderType {
    None,
    Basic,
    Bearer,
}
