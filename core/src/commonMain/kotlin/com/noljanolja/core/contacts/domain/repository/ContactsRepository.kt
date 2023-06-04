package com.noljanolja.core.contacts.domain.repository

import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.user.domain.model.User

internal interface ContactsRepository {
    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>
    suspend fun getContacts(page: Int): Result<List<User>>

    suspend fun findContacts(phoneNumber: String?, friendId: String?): Result<List<User>>
    suspend fun inviteFriend(friendId: String): Result<Boolean>
}