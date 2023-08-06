package com.noljanolja.android.features.home

sealed interface CheckinEvent {
    object Back : CheckinEvent
    object Checkin : CheckinEvent
    object Referral : CheckinEvent
}