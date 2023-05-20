package com.noljanolja.android.features.home.wallet.dashboard

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.features.home.wallet.model.toUiModel
import com.noljanolja.android.util.getMonth
import com.noljanolja.android.util.getYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletDashboardViewModel() : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<UiState<DashboardUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: WalletDashboardEvent) {
        launch {
            when (event) {
                WalletDashboardEvent.Back -> back()
            }
        }
    }

    fun setTime(month: Int, year: Int) {
        launch {
            val currentValue = _uiStateFlow.value
            _uiStateFlow.emit(
                currentValue.copy(
                    loading = true
                )
            )
            _uiStateFlow.emit(
                currentValue.copy(
                    data = DashboardUiData(
                        currentMonth = month,
                        currentYear = year,
                        transactions = currentValue.data?.transactions ?: mutableMapOf()
                    )
                )
            )
            if (currentValue.data?.transactions?.get("$month-$year").isNullOrEmpty()) {
                loadData(month, year)
            }
        }
    }

    private suspend fun loadData(month: Int, year: Int) {
        val currentValue = _uiStateFlow.value
        val data = currentValue.data
        val result = coreManager.getLoyaltyPoints(
            month = month,
            year = year
        )
        if (result.isSuccess) {
            val transactionsByMonth = result
                .getOrDefault(emptyList())
                .map { it.toUiModel() }
                .groupBy { it.createdAt.let { createdAt -> "${createdAt.getMonth()}-${createdAt.getYear()}" } }
            val newValue = currentValue.copy(
                data = data?.copy(
                    transactions = data.transactions + transactionsByMonth
                )
            )
            _uiStateFlow.emit(
                newValue
            )
        } else {
            result.exceptionOrNull().let {
                _uiStateFlow.emit(
                    currentValue.copy(
                        error = it
                    )
                )
                sendError(it!!)
            }
        }
    }
}

data class DashboardUiData(
    val currentMonth: Int = 0,
    val currentYear: Int = 0,
    val transactions: Map<String, List<UiLoyaltyPoint>> = mutableMapOf(),
)