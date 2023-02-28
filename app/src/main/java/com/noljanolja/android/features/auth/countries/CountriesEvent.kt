package com.noljanolja.android.features.auth.countries

sealed interface CountriesEvent {
    object Close : CountriesEvent
    data class SelectCountry(
        val countryCode: String,
    ) : CountriesEvent
}