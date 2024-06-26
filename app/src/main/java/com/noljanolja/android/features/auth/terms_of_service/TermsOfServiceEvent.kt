package com.noljanolja.android.features.auth.terms_of_service

sealed interface TermsOfServiceEvent {
    object Continue : TermsOfServiceEvent
    data class Detail(val index: Int) : TermsOfServiceEvent
}