package com.noljanolja.android.features.home.contacts

sealed interface ContactsEvent {

    data class SearchFriend(
        val text: String,
    ) : ContactsEvent

    object SyncContacts : ContactsEvent

    object OpenPhoneSettings : ContactsEvent

    object Back : ContactsEvent
}