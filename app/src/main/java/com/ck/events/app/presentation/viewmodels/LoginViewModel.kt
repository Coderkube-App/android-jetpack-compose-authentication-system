package com.ck.events.app.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.events.app.domain.model.AuthProvider
import com.ck.events.app.domain.repository.AuthError
import com.ck.events.app.domain.usecase.LoginUseCase
import com.ck.events.app.domain.usecase.SocialLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val socialLoginUseCase: SocialLoginUseCase
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                loginUseCase(email, password)
                loginSuccess = true
            } catch (e: AuthError) {
                errorMessage = e.message
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            errorMessage = "Google login failed: ID Token is null"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                socialLoginUseCase(AuthProvider.GOOGLE, idToken)
                loginSuccess = true
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Google login failed"
            } finally {
                isLoading = false
            }
        }
    }
}
