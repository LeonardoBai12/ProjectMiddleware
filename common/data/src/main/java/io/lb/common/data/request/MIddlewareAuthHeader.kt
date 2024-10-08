package io.lb.common.data.request

import kotlinx.serialization.Serializable

/**
 * Data class representing a middleware auth header.
 *
 * @property type The type of the middleware auth header.
 * @property token The token of the middleware auth header.
 */
@Serializable
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

    companion object {
        fun fromMap(map: Map<String, String>?): MiddlewareAuthHeader? {
            map ?: return null
            val token = map["Authorization"].orEmpty()
            val type = MiddlewareAuthHeaderType.entries.find {
                token.startsWith(it.name, ignoreCase = true)
            } ?: MiddlewareAuthHeaderType.None

            return MiddlewareAuthHeader(type, token)
        }
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
