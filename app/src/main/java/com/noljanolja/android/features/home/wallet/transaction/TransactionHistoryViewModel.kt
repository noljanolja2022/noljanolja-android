package com.noljanolja.android.features.home.wallet.transaction

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionHistoryViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<TransactionHistoryUiData>>(
        UiState(
            data = TransactionHistoryUiData(
                transactions = listOf(
                    LoyaltyPoint(),
                    LoyaltyPoint(),
                    LoyaltyPoint(),
                    LoyaltyPoint()
                )
            )
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: TransactionsHistoryEvent) {
        launch {
            when (event) {
                TransactionsHistoryEvent.Back -> navigationManager.navigate(NavigationDirections.Back)
            }
        }
    }
}

data class TransactionHistoryUiData(
    val filterType: TransactionFilterType = TransactionFilterType.All,
    val transactions: List<LoyaltyPoint>,
)

enum class TransactionFilterType(@StringRes titleId: Int) {
    All(R.string.wallet_my_point),
    Received(R.string.wallet_my_point),
    Exchange(R.string.wallet_my_point),
    BuyInShop(R.string.wallet_my_point),
}