package io.lb.data.util

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

enum class MiddlewareAuthHeaderType {
    None,
    Basic,
    Bearer,
}
