package com.noljanolja.android.features.home.wallet.detail

sealed interface TransactionDetailEvent {
    object Back : TransactionDetailEvent
}