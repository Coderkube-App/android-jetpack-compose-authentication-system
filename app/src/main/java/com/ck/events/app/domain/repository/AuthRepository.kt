package com.ck.events.app.domain.repository

import com.ck.events.app.domain.model.AuthProvider
import com.ck.events.app.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

sealed class AuthError(override val message: String) : Exception(message) {
    object InvalidCredentials : AuthError("Invalid email or password.")
    object UserNotFound : AuthError("User not found.")
    object EmailAlreadyInUse : AuthError("Email is already in use.")
    object NetworkError : AuthError("Network error. Please try again.")
    object SocialLoginCancelled : AuthError("Social login was cancelled.")
    data class Custom(val errorMessage: String) : AuthError(errorMessage)
    object Unknown : AuthError("An unknown error occurred.")
}

interface AuthRepository {
    val authSession: Flow<AuthSession?>
    suspend fun login(email: String, password: String): AuthSession
    suspend fun signup(email: String, password: String): AuthSession
    suspend fun socialLogin(provider: AuthProvider, idToken: String, accessToken: String?): AuthSession
    suspend fun logout()
}
