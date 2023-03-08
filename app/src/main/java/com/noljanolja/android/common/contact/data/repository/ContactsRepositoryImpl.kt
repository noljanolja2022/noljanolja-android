package com.noljanolja.android.common.contact.data.repository

import com.noljanolja.android.common.contact.data.ContactsLoader
import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.common.contact.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.toList

class ContactsRepositoryImpl(private val contactsLoader: ContactsLoader) : ContactsRepository {
    override suspend fun syncContacts(): Result<List<Contact>> {
        val contacts = contactsLoader.loadContacts().toList()
        return Result.success(contacts)
    }
}