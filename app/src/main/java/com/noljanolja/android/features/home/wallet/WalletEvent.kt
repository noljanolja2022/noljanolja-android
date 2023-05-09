package com.noljanolja.android.features.home.wallet

sealed interface WalletEvent {
    object Setting : WalletEvent
}