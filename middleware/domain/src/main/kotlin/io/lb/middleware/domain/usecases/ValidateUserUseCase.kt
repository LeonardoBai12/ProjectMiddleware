package io.lb.middleware.domain.usecases

import io.lb.middleware.domain.repository.MiddlewareRepository

class ValidateUserUseCase(
    private val repository: MiddlewareRepository
) {
    suspend operator fun invoke(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long,
    ): Boolean {
        return repository.validateUser(
            secret = secret,
            audience = audience,
            issuer = issuer,
            userId = userId,
            email = email,
            expiration = expiration
        )
    }
}
