package com.noljanolja.android.common.contact.domain.repository

import com.noljanolja.android.common.contact.domain.model.Contact

interface ContactsRepository {
    suspend fun syncContacts(): Result<List<Contact>>
}