package com.noljanolja.core.auth.domain.repository

import kotlinx.coroutines.flow.Flow

internal interface AuthRepository {

    suspend fun getAuthToken(): Flow<String?>

    suspend fun saveAuthToken(
        authToken: String,
    )

    suspend fun getPushToken(): String?

    suspend fun savePushToken(
        pushToken: String,
    )

    suspend fun delete()
}
