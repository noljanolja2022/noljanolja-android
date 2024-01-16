package com.noljanolja.core.contacts.domain.repository

import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.shop.domain.model.*
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.*

internal interface ContactsRepository {
    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>
    suspend fun getContacts(page: Int): Result<List<User>>

    suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>>
    suspend fun inviteFriend(friendId: String): Result<Boolean>

    suspend fun sendPoint(request: SendPointRequest): Result<UserSendPoint>

    suspend fun getPointConfig(): Result<PointConfig>

    suspend fun getNotifications(request: GetNotificationsRequest): Result<List<NotificationData>>

    suspend fun readNotification(request: ReadNotificationRequest): Result<String>

    suspend fun maskAllNotificationsIsRead(): Result<String>

    fun getSearchHistories(): Flow<List<SearchKey>>

    fun insertKey(text: String)

    suspend fun clearText(text: String)

    suspend fun clearAll()
}