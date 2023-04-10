package com.noljanolja.android.features.home.contacts

import com.noljanolja.core.user.domain.model.User

sealed interface ContactsEvent {
    object GetContacts : ContactsEvent

    object SyncContacts : ContactsEvent

    data class SearchContact(val searchText: String) : ContactsEvent

    object OpenPhoneSettings : ContactsEvent

    object Back : ContactsEvent

    data class SelectContact(val contact: User) : ContactsEvent

    object ConfirmContacts : ContactsEvent

    object LoadMore : ContactsEvent
}