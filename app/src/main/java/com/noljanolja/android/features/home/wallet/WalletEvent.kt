package com.noljanolja.android.features.home.wallet

sealed interface WalletEvent {
    object Setting : WalletEvent
    object TransactionHistory : WalletEvent
    object Ranking : WalletEvent
    object Refresh : WalletEvent
    object CheckIn : WalletEvent
}