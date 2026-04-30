package com.ck.events.app.domain.model

enum class AuthProvider {
    EMAIL,
    GOOGLE,
    CUSTOM
}

data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val provider: AuthProvider
)

data class AuthSession(
    val token: String,
    val user: User
)
