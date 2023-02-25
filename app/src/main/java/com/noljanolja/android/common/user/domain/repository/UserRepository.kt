package com.noljanolja.android.common.user.domain.repository

import android.content.Intent
import com.noljanolja.android.common.user.domain.model.User

interface UserRepository {
    fun getCurrentUser(): User?
    suspend fun sendRegistrationToServer(token: String)

    suspend fun getMe(forceRefresh: Boolean = false): Result<User>

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

    // logout

    suspend fun logout(): Result<Boolean>
}
