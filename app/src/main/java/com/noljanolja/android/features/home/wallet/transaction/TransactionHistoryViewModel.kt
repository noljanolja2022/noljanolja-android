package com.noljanolja.android.features.home.wallet.transaction

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.features.home.wallet.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionHistoryViewModel() : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<TransactionHistoryUiData>>(
        UiState(
            loading = true
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val result = coreManager.getLoyaltyPoints()
            if (result.isSuccess) {
                _uiStateFlow.emit(
                    UiState(
                        data = TransactionHistoryUiData(
                            transactions = result.getOrDefault(emptyList()).map { it.toUiModel() }
                        )
                    )
                )
            } else {
                result.exceptionOrNull().let {
                    _uiStateFlow.emit(
                        UiState(
                            error = it
                        )
                    )
                    sendError(it!!)
                }
            }
        }
    }

    fun handleEvent(event: TransactionsHistoryEvent) {
        launch {
            when (event) {
                TransactionsHistoryEvent.Back -> back()
                is TransactionsHistoryEvent.Dashboard -> navigationManager.navigate(
                    NavigationDirections.Dashboard(
                        event.month,
                        event.year
                    )
                )

                is TransactionsHistoryEvent.Detail -> navigationManager.navigate(
                    NavigationDirections.TransactionDetail(
                        event.transaction
                    )
                )
            }
        }
    }
}

data class TransactionHistoryUiData(
    val filterType: TransactionFilterType = TransactionFilterType.All,
    val transactions: List<UiLoyaltyPoint>,
)

enum class TransactionFilterType(@StringRes val titleId: Int) {
    All(R.string.wallet_my_point),
    Received(R.string.wallet_my_point),
    Exchange(R.string.wallet_my_point),
    BuyInShop(R.string.wallet_my_point),
}