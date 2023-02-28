package com.noljanolja.android.common.user.data.repository

import android.content.Intent
import android.util.Log
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.user.data.datasource.UserRemoteDataSource
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authSdk: AuthSdk,
    private val client: HttpClient,
) : UserRepository {
    private var _currentUser: User? = null
    override suspend fun getCurrentUser(forceRefresh: Boolean): Result<User> {
        return if (_currentUser != null && !forceRefresh) {
            Result.success(_currentUser!!)
        } else {
            userRemoteDataSource.getMe().also {
                _currentUser = it.getOrNull()
            }
        }
    }

    override suspend fun sendRegistrationToServer(token: String) {
        // TODO
        Log.e("USER_REPOSITORY", "SEND$token")
    }

    override suspend fun verifyOTPCode(verificationId: String, otp: String): Result<User> {
        val result = authSdk.verifyOTPCode(verificationId, otp)
        return handleResult(result)
    }

    // Google

    override suspend fun getAccountFromGoogleIntent(data: Intent?): Result<User> {
        val result = authSdk.getAccountFromGoogleIntent(data)
        return handleResult(result)
    }

    // Naver

    override suspend fun getAccountFromNaverIntent(data: Intent?): Result<User> {
        val result = authSdk.getAccountFromNaverIntent(data)
        return handleResult(result)
    }

    // Kakao

    override suspend fun loginWithKakao(): Result<User> {
        val result = authSdk.loginWithKakao()
        return handleResult(result)
    }

    // Email

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        val result = authSdk.signInWithEmailAndPassword(email, password)
        return handleResult(result)
    }

    // Logout
    override suspend fun logout(): Result<Boolean> {
        return authSdk.logOut().also {
            _currentUser = null
            val provider =
                client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()
            provider?.clearToken()
        }
    }

    private suspend fun <T> handleResult(result: Result<T>): Result<User> {
        return result.exceptionOrNull()?.let {
            Result.failure(it)
        } ?: getCurrentUser(true)
    }
}
