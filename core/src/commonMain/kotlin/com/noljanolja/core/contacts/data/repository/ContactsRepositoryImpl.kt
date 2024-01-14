package com.noljanolja.core.contacts.data.repository

import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.User

internal class ContactsRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
) : ContactsRepository {
    override suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return userRemoteDataSource.syncUserContacts(contacts)
    }

    override suspend fun getContacts(page: Int): Result<List<User>> {
        return userRemoteDataSource.getContacts(page)
    }

    override suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>> {
        return userRemoteDataSource.findContacts(phoneNumber, friendId)
    }

    override suspend fun inviteFriend(friendId: String): Result<Boolean> {
        return userRemoteDataSource.inviteFriend(friendId)
    }

    override suspend fun sendPoint(request: SendPointRequest): Result<UserSendPoint> {
        return userRemoteDataSource.sendPoint(request)
    }

    override suspend fun getPointConfig(): Result<PointConfig> {
        return userRemoteDataSource.getPointConfig()
    }

    override suspend fun getNotifications(request: GetNotificationsRequest): Result<List<NotificationData>> {
        return userRemoteDataSource.getNotifications(request)
    }
}