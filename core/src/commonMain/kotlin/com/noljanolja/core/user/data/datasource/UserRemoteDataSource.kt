package com.noljanolja.core.user.data.datasource

import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.user.data.model.request.DeviceType
import com.noljanolja.core.user.data.model.request.PushTokensRequest
import com.noljanolja.core.user.data.model.request.SyncUserContactsRequest
import com.noljanolja.core.user.data.model.request.UpdateUserRequest
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>

    suspend fun pushToken(userId: String, token: String): Result<Boolean>

    suspend fun updateUser(name: String?, email: String?): Result<Boolean>

    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>
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

    override suspend fun pushToken(userId: String, token: String): Result<Boolean> {
        return try {
            if (userId.isBlank()) {
                Result.failure<Boolean>(Exception("invalid arg: userId: $userId"))
            } else {
                val result = userApi.pushTokens(
                    PushTokensRequest(
                        userId = userId,
                        deviceToken = token,
                        deviceType = DeviceType.MOBILE
                    )
                )
                if (result.isSuccessful()) {
                    Result.success(true)
                } else {
                    Result.failure(Throwable(result.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return try {
            val response = userApi.syncUserContacts(SyncUserContactsRequest(contacts))
            if (response.isSuccessful()) {
                Result.success(response.data.map { it.toDomainUser() })
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(name: String?, email: String?): Result<Boolean> {
        return try {
            val response = userApi.updateUser(
                UpdateUserRequest(
                    name = name,
                    email = email
                )
            )
            if (response.isSuccessful()) {
                Result.success(true)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
