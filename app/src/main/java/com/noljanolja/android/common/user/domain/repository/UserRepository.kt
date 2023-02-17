package com.noljanolja.android.common.user.domain.repository

interface UserRepository {
    suspend fun sendRegistrationToServer(token: String)
}
