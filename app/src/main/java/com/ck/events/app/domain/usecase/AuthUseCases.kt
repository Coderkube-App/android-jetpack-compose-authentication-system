package com.ck.events.app.domain.usecase

import com.ck.events.app.domain.model.AuthSession
import com.ck.events.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthSession {
        return repository.login(email, password)
    }
}

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthSession {
        return repository.signup(email, password)
    }
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}

class ObserveAuthSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthSession?> {
        return repository.authSession
    }
}
class SocialLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(provider: com.ck.events.app.domain.model.AuthProvider, idToken: String): AuthSession {
        return repository.socialLogin(provider, idToken, null)
    }
}
