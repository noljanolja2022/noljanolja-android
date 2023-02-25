package com.noljanolja.android.common.user.data.datasource

import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.util.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>
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
}
