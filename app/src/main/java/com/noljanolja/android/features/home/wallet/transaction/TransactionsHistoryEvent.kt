package com.noljanolja.android.features.home.wallet.transaction

import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint

sealed interface TransactionsHistoryEvent {
    object Back : TransactionsHistoryEvent
    data class Dashboard(val month: Int, val year: Int) : TransactionsHistoryEvent
    data class Detail(val transaction: UiLoyaltyPoint) : TransactionsHistoryEvent
    data class Filter(val type: TransactionFilterType) : TransactionsHistoryEvent
}