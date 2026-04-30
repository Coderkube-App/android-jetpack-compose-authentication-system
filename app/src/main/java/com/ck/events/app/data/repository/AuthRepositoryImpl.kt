package com.ck.events.app.data.repository

import com.ck.events.app.data.source.AuthRemoteDataSource
import com.ck.events.app.domain.model.AuthProvider
import com.ck.events.app.domain.model.AuthSession
import com.ck.events.app.domain.model.User
import com.ck.events.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val authSession: Flow<AuthSession?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // We don't have the token here easily without another async call, 
                // but for session observation, the user object is enough.
                // In a real app, we might fetch the token if needed.
                trySend(
                    AuthSession(
                        token = "", // Token can be fetched on demand
                        user = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email,
                            displayName = firebaseUser.displayName,
                            provider = AuthProvider.EMAIL // Default to email, can be refined
                        )
                    )
                )
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun login(email: String, password: String): AuthSession {
        return remoteDataSource.login(email, password)
    }

    override suspend fun signup(email: String, password: String): AuthSession {
        return remoteDataSource.signup(email, password)
    }

    override suspend fun socialLogin(
        provider: AuthProvider,
        idToken: String,
        accessToken: String?
    ): AuthSession {
        return remoteDataSource.socialLogin(provider, idToken, accessToken)
    }

    override suspend fun logout() {
        remoteDataSource.logout()
    }
}
