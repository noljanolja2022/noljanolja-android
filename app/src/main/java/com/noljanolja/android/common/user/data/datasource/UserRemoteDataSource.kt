package com.noljanolja.android.common.user.data.datasource

import com.noljanolja.android.common.user.data.model.request.PushTokensRequest
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.util.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>

    suspend fun pushToken(token: String): Result<Boolean>
}

class UserRemoteDataSourceImpl(private val userApi: UserApi) : UserRemoteDataSource {
    override suspend fun getMe(): Result<User> {
        return try {
            val user = userApi.getMe().data.toDomainUser()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pushToken(token: String): Result<Boolean> {
        return try {
            val result = userApi.pushTokens(PushTokensRequest(token))
            if (result.isSuccessful()) {
                Result.success(true)
            } else {
                Result.failure(Throwable(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
