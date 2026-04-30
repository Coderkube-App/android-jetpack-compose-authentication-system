package com.ck.events.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.ck.events.app.presentation.navigation.AuthNavigation
import com.ck.events.app.presentation.navigation.Screen
import com.ck.events.app.presentation.viewmodels.MainViewModel
import com.ck.events.app.ui.theme.ComposeAuthSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAuthSystemTheme {
                val mainViewModel: MainViewModel = hiltViewModel()
                val startDestination = mainViewModel.startDestination

                // Default to Login screen to avoid splash loader 'freeze'
                AuthNavigation(startDestination = startDestination ?: Screen.Login.route)
            }
        }
    }
}