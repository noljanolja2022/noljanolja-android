package com.noljanolja.core.user.domain.repository

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

    suspend fun updateUser(
        name: String,
        photo: String?,
    ): Result<User>

    // logout
    suspend fun logout(): Result<Boolean>
}
