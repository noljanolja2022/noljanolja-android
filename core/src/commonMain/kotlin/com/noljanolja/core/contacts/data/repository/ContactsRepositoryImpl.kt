package com.noljanolja.core.contacts.data.repository

import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.contacts.domain.repository.*
import com.noljanolja.core.shop.data.datasource.*
import com.noljanolja.core.shop.domain.model.*
import com.noljanolja.core.user.data.datasource.*
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.flow.*

internal class ContactsRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val searchLocalDatasource: SearchLocalDatasource,
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

    override suspend fun readNotification(request: ReadNotificationRequest): Result<String> {
        return userRemoteDataSource.readNotification(request)
    }

    override suspend fun maskAllNotificationsIsRead(): Result<String> {
        return userRemoteDataSource.maskAllNotificationsIsRead()
    }

    override fun getSearchHistories(): Flow<List<SearchKey>> {
        return searchLocalDatasource.findAllByScreen(screen = SCREEN)
    }

    override fun insertKey(text: String) {
        searchLocalDatasource.insertKey(text, screen = SCREEN)
    }

    override suspend fun clearText(text: String) {
        searchLocalDatasource.deleteByText(text, screen = SCREEN)
    }

    override suspend fun clearAll() {
        searchLocalDatasource.deleteByScreen(SCREEN)
    }

    companion object {
        private const val SCREEN = "CONTACT"
    }
}