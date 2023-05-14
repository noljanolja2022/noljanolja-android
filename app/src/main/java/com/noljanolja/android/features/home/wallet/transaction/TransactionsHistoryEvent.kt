package com.noljanolja.android.features.home.wallet.transaction

sealed interface TransactionsHistoryEvent {
    object Back : TransactionsHistoryEvent
}