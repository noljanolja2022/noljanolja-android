package com.noljanolja.core.auth.domain.repository

interface AuthRepository {

    suspend fun getAuthToken(): String?

    suspend fun saveAuthToken(
        authToken: String,
    )

    suspend fun getPushToken(): String?

    suspend fun savePushToken(
        pushToken: String,
    )

    suspend fun delete()
}
