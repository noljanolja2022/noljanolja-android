package com.noljanolja.core.user.domain.repository

import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.CheckinProgress
import com.noljanolja.core.user.domain.model.User

internal interface UserRepository {
    suspend fun getCurrentUser(
        forceRefresh: Boolean = false,
        onlyLocal: Boolean = false,
    ): Result<User>

    suspend fun pushTokens(token: String): Result<Boolean>

    // Phone
    suspend fun verifyOTPCode(verificationId: String, otp: String): Result<String>

//    // Google
//    suspend fun getAccountFromGoogleIntent(data: Intent?): Result<User>
//
//    // Naver
//    suspend fun getAccountFromNaverIntent(data: Intent?): Result<User>

//    // Kakao
//    suspend fun loginWithKakao(): Result<User>

//    // Email
//    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun updateUser(request: UpdateUserRequest): Result<User>

    suspend fun updateAvatar(
        name: String,
        type: String,
        files: ByteArray,
    ): Result<Boolean>

    // logout
    suspend fun logout(requireSuccess: Boolean): Result<Boolean>

    suspend fun checkin(): Result<String>

    suspend fun getCheckinProgress(): Result<List<CheckinProgress>>

    suspend fun addReferralCode(code: String): Result<Long>
}
