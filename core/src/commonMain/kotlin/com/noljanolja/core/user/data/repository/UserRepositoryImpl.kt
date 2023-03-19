package com.noljanolja.core.user.data.repository

import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.conversation.data.datasource.LocalConversationDataSource
import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authDataSource: AuthDataSource,
    private val client: HttpClient,
    private val authRepository: AuthRepository,
    private val localUserDataSource: LocalUserDataSource,
    private val localConversationDataSource: LocalConversationDataSource,
) : UserRepository {

    // Remote
    override suspend fun getCurrentUser(forceRefresh: Boolean): Result<User> {
        val currentUser = localUserDataSource.findMe()
        return if (currentUser != null && !forceRefresh) {
            Result.success(currentUser)
        } else {
            userRemoteDataSource.getMe().also {
                it.getOrNull()?.let {
                    localUserDataSource.upsert(it.apply { isMe = true })
                    authRepository.getPushToken()?.let {
                        pushTokens(it)
                    }
                }
            }
        }
    }

    override suspend fun pushTokens(
        token: String,
    ): Result<Boolean> {
        val currentUser = localUserDataSource.findMe()
        return userRemoteDataSource.pushToken(userId = currentUser?.id.orEmpty(), token)
    }

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
        return pushTokens("").also {
            if (it.isSuccess) {
                authDataSource.logout().also {
                    val provider =
                        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
                            .firstOrNull()
                    provider?.clearToken()
                    authRepository.saveAuthToken("")
                    localConversationDataSource.deleteAll()
                    localConversationDataSource.deleteAllMessages()
                    localUserDataSource.deleteAllParticipants()
                    localUserDataSource.deleteAll()
                }
            }
        }
    }

    private suspend fun <T> handleResult(result: Result<T>): Result<User> {
        return result.exceptionOrNull()?.let {
            Result.failure(it)
        } ?: getCurrentUser(true)
    }
}
