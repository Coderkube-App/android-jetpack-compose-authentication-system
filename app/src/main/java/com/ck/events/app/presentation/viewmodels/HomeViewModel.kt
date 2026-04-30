package com.ck.events.app.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.events.app.domain.usecase.LogoutUseCase
import com.ck.events.app.domain.usecase.ObserveAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    var userEmail by mutableStateOf("Loading...")
    var userProvider by mutableStateOf("Loading...")
    var showingLogoutAlert by mutableStateOf(false)
    var logoutSuccess by mutableStateOf(false)

    init {
        viewModelScope.launch {
            observeAuthSessionUseCase().collectLatest { session ->
                if (session != null) {
                    userEmail = session.user.email ?: "No Email"
                    userProvider = session.user.provider.name
                } else {
                    logoutSuccess = true
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
