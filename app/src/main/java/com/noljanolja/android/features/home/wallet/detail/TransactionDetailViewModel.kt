package com.noljanolja.android.features.home.wallet.detail

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch

class TransactionDetailViewModel : BaseViewModel() {
    fun handleEvent(event: TransactionDetailEvent) {
        launch {
            when (event) {
                TransactionDetailEvent.Back -> back()
            }
        }
    }
}