package com.noljanolja.core.user.data.model.request

import com.noljanolja.core.contacts.domain.model.Contact
import kotlinx.serialization.Serializable

@Serializable
data class SyncUserContactsRequest(
    val contacts: List<Contact>,
)