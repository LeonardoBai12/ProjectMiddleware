package io.lb.impl.ktor.client.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String,
    val userName: String,
    val phone: String,
    val password: String?,
    val email: String,
    val profilePictureUrl: String
)
