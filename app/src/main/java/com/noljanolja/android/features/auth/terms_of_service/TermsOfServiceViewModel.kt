package com.noljanolja.android.features.auth.terms_of_service

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : BaseViewModel() {

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