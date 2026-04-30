package com.ck.events.app.data.source

import com.ck.events.app.domain.model.AuthProvider
import com.ck.events.app.domain.model.AuthSession
import com.ck.events.app.domain.model.User
import com.ck.events.app.domain.repository.AuthError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): AuthSession
    suspend fun signup(email: String, password: String): AuthSession
    suspend fun socialLogin(provider: AuthProvider, idToken: String, accessToken: String?): AuthSession
    suspend fun logout()
}

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): AuthSession {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw AuthError.UserNotFound
            val token = firebaseUser.getIdToken(false).await().token ?: ""
            
            AuthSession(
                token = token,
                user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName,
                    provider = AuthProvider.EMAIL
                )
            )
        } catch (e: Exception) {
            throw mapFirebaseError(e)
        }
    }

    override suspend fun signup(email: String, password: String): AuthSession {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw AuthError.Unknown
            val token = firebaseUser.getIdToken(false).await().token ?: ""
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                provider = AuthProvider.EMAIL
            )

            // Save to Firestore
            saveUserToFirestore(user)
            
            AuthSession(token = token, user = user)
        } catch (e: Exception) {
            throw mapFirebaseError(e)
        }
    }

    override suspend fun socialLogin(
        provider: AuthProvider,
        idToken: String,
        accessToken: String?
    ): AuthSession {
        return try {
            val credential = when (provider) {
                AuthProvider.GOOGLE -> {
                    GoogleAuthProvider.getCredential(idToken, null)
                }
                else -> throw AuthError.InvalidCredentials
            }

            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw AuthError.Unknown
            val token = firebaseUser.getIdToken(false).await().token ?: ""
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                provider = provider
            )

            // Save or Update in Firestore
            saveUserToFirestore(user)
            
            AuthSession(token = token, user = user)
        } catch (e: Exception) {
            throw mapFirebaseError(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    private suspend fun saveUserToFirestore(user: User) {
        val userMap = hashMapOf(
            "id" to user.id,
            "email" to user.email,
            "displayName" to user.displayName,
            "provider" to user.provider.name,
            "createdAt" to System.currentTimeMillis()
        )
        
        firestore.collection("users")
            .document(user.id)
            .set(userMap)
            .await()
    }

    private fun mapFirebaseError(e: Exception): AuthError {
        val exception = if (e is java.util.concurrent.ExecutionException) e.cause ?: e else e
        if (exception is FirebaseAuthException) {
            return when (exception.errorCode) {
                "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD" -> AuthError.InvalidCredentials
                "ERROR_USER_NOT_FOUND" -> AuthError.UserNotFound
                "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EmailAlreadyInUse
                "ERROR_NETWORK_REQUEST_FAILED" -> AuthError.NetworkError
                "ERROR_WEAK_PASSWORD" -> AuthError.Custom("The password is too weak.")
                "ERROR_INVALID_EMAIL" -> AuthError.Custom("The email address is badly formatted.")
                else -> AuthError.Custom(exception.localizedMessage ?: "Firebase error: ${exception.errorCode}")
            }
        }
        return AuthError.Custom(exception.localizedMessage ?: "Unknown error occurred")
    }
}
