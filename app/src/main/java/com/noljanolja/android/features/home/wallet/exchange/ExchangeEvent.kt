package com.noljanolja.android.features.home.wallet.exchange

sealed interface ExchangeEvent {
    object Back : ExchangeEvent
    object Convert : ExchangeEvent
}