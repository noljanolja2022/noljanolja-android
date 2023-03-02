package com.noljanolja.android.common.contact.domain.repository

import com.noljanolja.android.common.contact.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun syncContacts(): Flow<Result<List<Contact>>>
}