package com.noljanolja.android.features.auth.terms_of_service

import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*

class TermsOfServiceViewModel : BaseViewModel() {

    fun handleEvent(event: TermsOfServiceEvent) {
        launch {
            when (event) {
                is TermsOfServiceEvent.Continue -> {
                    navigationManager.navigate(NavigationDirections.UpdateProfile)
                }

                is TermsOfServiceEvent.Detail -> {
                    navigationManager.navigate(NavigationDirections.TermDetail(event.index))
                }
            }
        }
    }
}