package com.noljanolja.android.features.sharemessage

sealed interface SelectShareMessageEvent {
    data class Select(val contact: ShareContact) : SelectShareMessageEvent
    object Back : SelectShareMessageEvent

    object Share : SelectShareMessageEvent
}