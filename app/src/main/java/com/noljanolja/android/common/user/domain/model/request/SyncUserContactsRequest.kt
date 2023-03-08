package com.noljanolja.android.common.user.domain.model.request

import com.noljanolja.android.common.contact.domain.model.Contact
import kotlinx.serialization.Serializable

@Serializable
data class SyncUserContactsRequest(
    val contacts: List<Contact>,
)