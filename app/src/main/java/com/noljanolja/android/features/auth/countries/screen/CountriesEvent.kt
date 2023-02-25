package com.noljanolja.android.features.auth.countries.screen

sealed interface CountriesEvent {
    object Close : CountriesEvent
    data class SelectCountry(
        val countryCode: String,
    ) : CountriesEvent
}