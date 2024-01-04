package com.noljanolja.core.contacts.domain.repository

import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.User

internal interface ContactsRepository {
    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>
    suspend fun getContacts(page: Int): Result<List<User>>

    suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>>
    suspend fun inviteFriend(friendId: String): Result<Boolean>

    suspend fun sendPoint(request: SendPointRequest): Result<UserSendPoint>

    suspend fun getPointConfig(): Result<PointConfig>
}