package com.noljanolja.android.features.home.contacts

import com.noljanolja.core.user.domain.model.User

sealed interface ContactsEvent {
    object SyncContacts : ContactsEvent

    object OpenPhoneSettings : ContactsEvent

    object Back : ContactsEvent

    data class Chat(val contact: User) : ContactsEvent
}