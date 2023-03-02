package com.noljanolja.android.common.contact.data.repository

import com.noljanolja.android.common.contact.data.ContactsLoader
import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.common.contact.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

class ContactsRepositoryImpl(private val contactsLoader: ContactsLoader) : ContactsRepository {
    override fun syncContacts(): Flow<Result<List<Contact>>> = flow {
        val contacts = contactsLoader.loadContacts().toList()
        emit(Result.success(contacts))
    }
}