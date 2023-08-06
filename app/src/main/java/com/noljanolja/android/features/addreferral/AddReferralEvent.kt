package com.noljanolja.android.features.addreferral

sealed interface AddReferralEvent {
    object GoToMain : AddReferralEvent
    data class SendCode(val code: String) : AddReferralEvent
}