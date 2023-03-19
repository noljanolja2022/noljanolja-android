package com.noljanolja.android.common.user.data

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.core.CoreManager
import com.noljanolja.socket.TokenRepo

class TokenRepoImpl(
    private val coreManager: CoreManager,
    private val authSdk: AuthSdk,
) : TokenRepo {
    override suspend fun getToken(): String? {
        return coreManager.getAuthToken()
    }

    override suspend fun refreshToken() {
        coreManager.saveAuthToken(
            authSdk.getIdToken(true).orEmpty()
        )
    }
}