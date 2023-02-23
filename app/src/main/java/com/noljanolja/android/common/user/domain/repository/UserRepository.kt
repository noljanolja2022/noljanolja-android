package com.noljanolja.android.common.user.domain.repository

import com.noljanolja.android.common.user.domain.model.User

interface UserRepository {
    fun getCurrentUser(): User?
    suspend fun sendRegistrationToServer(token: String)

    suspend fun getMe(): Result<User>
}
