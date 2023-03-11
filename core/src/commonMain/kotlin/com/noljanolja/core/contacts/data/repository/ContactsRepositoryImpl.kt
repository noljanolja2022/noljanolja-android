package com.noljanolja.core.contacts.data.repository

import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.domain.model.User

class ContactsRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
) : ContactsRepository {
    override suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return userRemoteDataSource.syncUserContacts(contacts)
    }
}