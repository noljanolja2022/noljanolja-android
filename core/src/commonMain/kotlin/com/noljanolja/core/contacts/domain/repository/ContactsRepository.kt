package com.noljanolja.core.contacts.domain.repository

import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.user.domain.model.User

interface ContactsRepository {
    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>>
}