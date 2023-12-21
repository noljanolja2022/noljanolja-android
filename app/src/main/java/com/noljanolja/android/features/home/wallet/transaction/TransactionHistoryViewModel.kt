package com.noljanolja.android.features.home.wallet.transaction

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.features.home.wallet.model.*
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TransactionHistoryViewModel() : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<TransactionHistoryUiData>>(
        UiState(
            loading = true,
            data = TransactionHistoryUiData()
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            _uiStateFlow.map { it.data?.filterType }.filterNotNull().collect {
                val value = _uiStateFlow.value
                val type = when (it) {
                    TransactionFilterType.Received -> LoyaltyType.RECEIVE
                    TransactionFilterType.Spent -> LoyaltyType.SPENT
                    else -> null
                }
                val result = coreManager.getLoyaltyPoints(type)
                if (result.isSuccess) {
                    _uiStateFlow.emit(
                        UiState(
                            data = value.data?.copy(
                                transactions = result.getOrDefault(emptyList())
                                    .map { it.toUiModel() }
                            )
                        )
                    )
                } else {
                    result.exceptionOrNull().let { err ->
                        _uiStateFlow.emit(
                            value.copy(error = err)
                        )
                        sendError(err!!)
                    }
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

                is TransactionsHistoryEvent.Filter -> changeFilter(event.type)
            }
        }
    }

    private suspend fun changeFilter(filterType: TransactionFilterType) {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(
            UiState(
                data = value.data?.copy(
                    filterType = filterType
                )
            )
        )
    }
}

data class TransactionHistoryUiData(
    val filterType: TransactionFilterType = TransactionFilterType.All,
    val transactions: List<UiLoyaltyPoint> = emptyList(),
)

enum class TransactionFilterType(@StringRes val titleId: Int) {
    All(R.string.common_all),
    Received(R.string.transaction_receive_type),
    Spent(R.string.transaction_spent_type);

    //    BuyInShop(R.string.wallet_my_point),
    fun convertToUiLoyaltyPointType() = when (this) {
        Received -> Type.RECEIVE
        Spent -> Type.SPENT
        else -> null
    }
}