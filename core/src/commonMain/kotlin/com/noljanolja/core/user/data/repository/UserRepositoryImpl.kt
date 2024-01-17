package com.noljanolja.core.user.data.repository

import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.conversation.data.datasource.LocalConversationDataSource
import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.CheckinProgress
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import io.ktor.client.*

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authDataSource: AuthDataSource,
    private val client: HttpClient,
    private val authRepository: AuthRepository,
    private val localUserDataSource: LocalUserDataSource,
    private val localConversationDataSource: LocalConversationDataSource,
) : UserRepository {

    // Remote
    override suspend fun getCurrentUser(
        forceRefresh: Boolean,
        onlyLocal: Boolean,
    ): Result<User> {
        val currentUser = localUserDataSource.findMe()
        return when {
            onlyLocal -> {
                currentUser?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Cannot get user"))
            }

            forceRefresh || currentUser == null -> {
                userRemoteDataSource.getMe().also {
                    it.getOrNull()?.let {
                        localUserDataSource.upsert(it.apply { isMe = true })
                        authRepository.getPushToken()?.let {
                            pushTokens(it)
                        }
                    }
                }
            }

            else -> Result.success(currentUser)
        }
    }

    override suspend fun pushTokens(
        token: String,
    ): Result<Boolean> {
        val currentUser = localUserDataSource.findMe()
        return userRemoteDataSource.pushToken(userId = currentUser?.id.orEmpty(), token)
    }

    // Firebase

    override suspend fun verifyOTPCode(verificationId: String, otp: String): Result<String> {
        return authDataSource.verifyOTPCode(verificationId, otp)
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
    override suspend fun updateUser(request: UpdateUserRequest): Result<User> {
        val result = userRemoteDataSource.updateUser(request)
        return handleResult(result)
    }

    override suspend fun updateAvatar(
        name: String,
        type: String,
        files: ByteArray,
    ): Result<Boolean> {
        return userRemoteDataSource.updateAvatar(name, type, files)
    }

    // Logout
    override suspend fun logout(requireSuccess: Boolean): Result<Boolean> {
        return pushTokens("").also {
            if (it.isSuccess || !requireSuccess) {
                deleteAll()
            }
        }
    }

    private suspend fun deleteAll() {
        authDataSource.logout()
    }

    private suspend fun <T> handleResult(result: Result<T>): Result<User> {
        return result.exceptionOrNull()?.let {
            Result.failure(it)
        } ?: getCurrentUser(true)
    }

    override suspend fun checkin(): Result<String> {
        return userRemoteDataSource.checkin()
    }

    override suspend fun getCheckinProgress(): Result<List<CheckinProgress>> {
        return userRemoteDataSource.getCheckinProgress()
    }

    override suspend fun addReferralCode(code: String): Result<Long> {
        return userRemoteDataSource.addReferralCode(code)
    }
}
