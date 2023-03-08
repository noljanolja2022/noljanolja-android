package com.noljanolja.android.common.user.data.datasource

import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.model.request.PushTokensRequest
import com.noljanolja.android.common.user.domain.model.request.SyncUserContactsRequest
import com.noljanolja.android.util.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>

    suspend fun pushToken(token: String): Result<Boolean>

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

    override suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return try {
            val response = userApi.syncUserContacts(SyncUserContactsRequest(contacts))
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
