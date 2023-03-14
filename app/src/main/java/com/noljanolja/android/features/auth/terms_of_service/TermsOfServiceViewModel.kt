package com.noljanolja.android.features.auth.terms_of_service

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections

class TermsOfServiceViewModel : BaseViewModel() {

    fun handleEvent(event: TermsOfServiceEvent) {
        launch {
            when (event) {
                is TermsOfServiceEvent.Continue -> {
                    navigationManager.navigate(NavigationDirections.Auth)
                }
            }
        }
    }
}