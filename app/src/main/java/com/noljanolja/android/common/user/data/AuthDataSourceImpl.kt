package com.noljanolja.android.common.user.data

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.core.user.data.datasource.AuthDataSource

class AuthDataSourceImpl(private val authSdk: AuthSdk) : AuthDataSource {
    override suspend fun verifyOTPCode(verificationId: String, otp: String): Result<String> {
        return authSdk.verifyOTPCode(verificationId, otp)
    }

    override suspend fun logout(): Result<Boolean> {
        return authSdk.logOut()
    }
}