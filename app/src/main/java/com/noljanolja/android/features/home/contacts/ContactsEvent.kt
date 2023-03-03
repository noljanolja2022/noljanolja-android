package com.noljanolja.android.features.home.contacts

sealed interface ContactsEvent {
    object SyncContacts : ContactsEvent

    object OpenPhoneSettings : ContactsEvent

    object Back : ContactsEvent
}