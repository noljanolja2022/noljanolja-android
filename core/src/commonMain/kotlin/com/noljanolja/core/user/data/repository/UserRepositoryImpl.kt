package com.noljanolja.core.user.data.repository

import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.utils.Database
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authDataSource: AuthDataSource,
    private val client: HttpClient,
) : UserRepository {
    private var _currentUser: User? = null
        set(value) {
            User.currentUserId = value?.id
            field = value?.apply { isMe = true }
        }

    // Remote
    override suspend fun getCurrentUser(forceRefresh: Boolean): Result<User> {
        return if (_currentUser != null && !forceRefresh) {
            Result.success(_currentUser!!)
        } else {
            userRemoteDataSource.getMe().also {
                _currentUser = it.getOrNull()
            }
        }
    }

    override suspend fun pushTokens(
        token: String,
    ): Result<Boolean> = userRemoteDataSource.pushToken(token)

    // Firebase

    override suspend fun verifyOTPCode(verificationId: String, otp: String): Result<User> {
        val result = authDataSource.verifyOTPCode(verificationId, otp)
        return handleResult(result)
    }

    // Google

//    override suspend fun getAccountFromGoogleIntent(data: Intent?): Result<User> {
//        val result = authSdk.getAccountFromGoogleIntent(data)
//        return handleResult(result)
//    }
//
//    // Naver
//
//    override suspend fun getAccountFromNaverIntent(data: Intent?): Result<User> {
//        val result = authSdk.getAccountFromNaverIntent(data)
//        return handleResult(result)
//    }
//
//    // Kakao
//
//    override suspend fun loginWithKakao(): Result<User> {
//        val result = authSdk.loginWithKakao()
//        return handleResult(result)
//    }
//
//    // Email
//
//    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
//        val result = authSdk.signInWithEmailAndPassword(email, password)
//        return handleResult(result)
//    }

    // Update user
    override suspend fun updateUser(
        name: String,
        photo: String?,
    ): Result<User> {
        val result = userRemoteDataSource.updateUser(name, photo)
        return handleResult(result)
    }

    // Logout
    override suspend fun logout(): Result<Boolean> {
        return authDataSource.logout().also {
            _currentUser = null
            val provider =
                client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()
            provider?.clearToken()
            Database.clear()
        }
    }

    private suspend fun <T> handleResult(result: Result<T>): Result<User> {
        return result.exceptionOrNull()?.let {
            Result.failure(it)
        } ?: getCurrentUser(true)
    }
}
