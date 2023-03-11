package com.noljanolja.core.user.data.datasource

interface AuthDataSource {
    suspend fun verifyOTPCode(verificationId: String, otp: String): Result<String>

    suspend fun logout(): Result<Boolean>
}