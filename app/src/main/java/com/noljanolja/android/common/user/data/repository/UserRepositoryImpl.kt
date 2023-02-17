package com.noljanolja.android.common.user.data.repository

import android.util.Log
import com.noljanolja.android.common.user.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override suspend fun sendRegistrationToServer(token: String) {
        // TODO
        Log.e("USER_REPOSITORY", "SEND$token")
    }
}
