package com.ck.events.app.presentation.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ck.events.app.R
import com.ck.events.app.presentation.components.CustomTextField
import com.ck.events.app.presentation.components.PrimaryButton
import com.ck.events.app.presentation.components.SocialIconButton
import com.ck.events.app.presentation.viewmodels.LoginViewModel
import com.ck.events.app.ui.theme.BluePrimary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val serverClientId = "YOUR_CLIENT_ID_HERE"

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(serverClientId)
            .requestId()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.onGoogleSignInResult(account.idToken)
            } catch (e: ApiException) {
                viewModel.onGoogleSignInResult(null)
            }
        }
    }

    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFF2F2F7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = BluePrimary,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Sign in to continue",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    letterSpacing = 0.2.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Input Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    placeholder = "Email",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                CustomTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    placeholder = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true
                )
            }

            // Error Message
            AnimatedVisibility(
                visible = viewModel.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            PrimaryButton(
                text = "Log In",
                isLoading = viewModel.isLoading,
                onClick = { viewModel.login() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Signup Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToSignup) {
                    Text(
                        text = "Sign Up",
                        color = BluePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Social Login Divider
            Row(
                modifier = Modifier
                    .padding(horizontal = 64.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SocialIconButton(
                    painter = painterResource(id = R.drawable.ic_google),
                    onClick = {
                        startForResult.launch(googleSignInClient.signInIntent)
                    }
                )
            }
        }
    }
}
