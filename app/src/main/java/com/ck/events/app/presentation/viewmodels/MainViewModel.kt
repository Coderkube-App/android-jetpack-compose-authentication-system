package com.ck.events.app.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.events.app.domain.usecase.ObserveAuthSessionUseCase
import com.ck.events.app.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase
) : ViewModel() {

    var startDestination by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            observeAuthSessionUseCase().collectLatest { session ->
                if (session != null) {
                    startDestination = Screen.Home.route
                } else {
                    startDestination = Screen.Login.route
                }
            }
        }
    }
}
