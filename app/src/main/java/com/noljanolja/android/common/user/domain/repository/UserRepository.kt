package com.noljanolja.android.common.user.domain.repository

import android.content.Intent
import com.noljanolja.android.common.user.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(forceRefresh: Boolean = false): Result<User>

    suspend fun sendRegistrationToServer(token: String)

    // Phone
    suspend fun verifyOTPCode(verificationId: String, otp: String): Result<User>

    // Google
    suspend fun getAccountFromGoogleIntent(data: Intent?): Result<User>

    // Naver
    suspend fun getAccountFromNaverIntent(data: Intent?): Result<User>

    // Kakao
    suspend fun loginWithKakao(): Result<User>

    // Email
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun updateUser(
        name: String,
        photo: String?,
    ): Result<User>

    // logout

    suspend fun logout(): Result<Boolean>
}
