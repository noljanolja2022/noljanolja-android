package com.noljanolja.android.features.sharemessage

import com.noljanolja.android.features.common.ShareContact

sealed interface SelectShareMessageEvent {
    data class Select(val contact: ShareContact) : SelectShareMessageEvent
    object Back : SelectShareMessageEvent

    object Share : SelectShareMessageEvent
}