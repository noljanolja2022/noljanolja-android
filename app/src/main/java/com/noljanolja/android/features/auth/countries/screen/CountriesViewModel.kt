package com.noljanolja.android.features.auth.countries.screen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : BaseViewModel() {

    fun handleEvent(event: CountriesEvent) {
        launch {
            val direction = when (event) {
                is CountriesEvent.Close -> NavigationDirections.Back
                is CountriesEvent.SelectCountry -> NavigationDirections.FinishWithResults(
                    mapOf("countryCode" to event.countryCode)
                )
            }
            navigationManager.navigate(direction)
        }
    }
}