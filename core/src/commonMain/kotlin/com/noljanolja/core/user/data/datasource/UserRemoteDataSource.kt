package com.noljanolja.core.user.data.datasource

import co.touchlab.kermit.Logger
import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.user.data.model.request.DeviceType
import com.noljanolja.core.user.data.model.request.FindContactRequest
import com.noljanolja.core.user.data.model.request.InviteFriendRequest
import com.noljanolja.core.user.data.model.request.PushTokensRequest
import com.noljanolja.core.user.data.model.request.SyncUserContactsRequest
import com.noljanolja.core.user.data.model.request.UpdateAvatarRequest
import com.noljanolja.core.user.data.model.request.UpdateUserRequest
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>

    suspend fun pushToken(userId: String, token: String): Result<Boolean>

    suspend fun updateUser(name: String?, email: String?): Result<Boolean>

    suspend fun updateAvatar(field: String, type: String, files: ByteArray): Result<Boolean>

    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>

    suspend fun getContacts(page: Int): Result<List<User>>

    suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>>

    suspend fun inviteFriend(friendId: String): Result<Boolean>
}

class UserRemoteDataSourceImpl(private val userApi: UserApi) : UserRemoteDataSource {
    override suspend fun getMe(): Result<User> {
        return try {
            val user = userApi.getMe().data.toDomainUser()
            Result.success(user)
        } catch (e: Throwable) {
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
        } catch (e: Throwable) {
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
        } catch (e: Throwable) {
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
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun updateAvatar(
        name: String,
        type: String,
        files: ByteArray,
    ): Result<Boolean> {
        return try {
            Logger.d("Update avatar: $files")
            val response = userApi.updateAvatar(
                UpdateAvatarRequest(
                    name = name,
                    type = type,
                    files = files
                )
            )
            if (response.isSuccessful()) {
                Result.success(true)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getContacts(page: Int): Result<List<User>> {
        return try {
            val response = userApi.getContacts(page)
            if (response.isSuccessful()) {
                Result.success(response.data.map { it.toDomainUser() })
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>> {
        return try {
            val response = userApi.findContacts(FindContactRequest(phoneNumber, friendId))
            if (response.isSuccessful()) {
                Result.success(response.data.map { it.toDomainUser() })
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun inviteFriend(friendId: String): Result<Boolean> {
        return try {
            val response = userApi.inviteFriend(InviteFriendRequest(friendId))
            if (response.isSuccessful()) {
                Result.success(true)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
