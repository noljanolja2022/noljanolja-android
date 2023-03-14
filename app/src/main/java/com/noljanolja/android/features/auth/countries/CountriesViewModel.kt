package com.noljanolja.android.features.auth.countries

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections

class CountriesViewModel : BaseViewModel() {

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
