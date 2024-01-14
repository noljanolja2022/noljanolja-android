package com.noljanolja.core.user.data.datasource

import co.touchlab.kermit.Logger
import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.CheckinProgress
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.toDomainUser

interface UserRemoteDataSource {
    suspend fun getMe(): Result<User>

    suspend fun pushToken(userId: String, token: String): Result<Boolean>

    suspend fun updateUser(name: String?, email: String?, phone: String?): Result<Boolean>

    suspend fun updateAvatar(field: String, type: String, files: ByteArray): Result<Boolean>

    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>

    suspend fun getContacts(page: Int): Result<List<User>>

    suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>>

    suspend fun inviteFriend(friendId: String): Result<Boolean>

    suspend fun sendPoint(request: SendPointRequest): Result<UserSendPoint>

    suspend fun getPointConfig(): Result<PointConfig>

    suspend fun getNotifications(request: GetNotificationsRequest): Result<List<NotificationData>>

    suspend fun checkin(): Result<String>

    suspend fun getCheckinProgress(): Result<List<CheckinProgress>>

    suspend fun addReferralCode(code: String): Result<Long>
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

    override suspend fun updateUser(
        name: String?,
        email: String?,
        phone: String?,
    ): Result<Boolean> {
        return try {
            val response = userApi.updateUser(
                UpdateUserRequest(
                    name = name,
                    email = email,
                    phone = phone
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

    override suspend fun sendPoint(request: SendPointRequest): Result<UserSendPoint> {
        return try {
            val response = userApi.sendPoint(request)
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getPointConfig(): Result<PointConfig> {
        return try {
            val response = userApi.getPointConfig()
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getNotifications(request: GetNotificationsRequest): Result<List<NotificationData>> {
        return try {
            val response = userApi.getNotifications(request)
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun checkin(): Result<String> {
        return try {
            val response = userApi.checkin()
            if (response.isSuccessful()) {
                Result.success(response.message)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getCheckinProgress(): Result<List<CheckinProgress>> {
        return try {
            val response = userApi.getCheckinProgress()
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun addReferralCode(code: String): Result<Long> {
        return try {
            val response = userApi.addReferralCode(AddReferralCodeRequest(code))
            if (response.isSuccessful()) {
                Result.success(response.data.rewardPoints)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
