package com.noljanolja.android.features.shop.search

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchProductViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(
        UiState(
            loading = false,
            data = SearchGiftUiData()
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    private val _myBalanceFlow = MutableStateFlow<ExchangeBalance>(ExchangeBalance())
    val myBalanceFlow = _myBalanceFlow.asStateFlow()

    val searchKeys = coreManager.getSearchHistories().map {
        it.sortedByDescending { it.updatedAt }.map { it.text }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        launch {
            val myBalance = coreManager.getExchangeBalance().getOrDefault(ExchangeBalance())
            _myBalanceFlow.emit(myBalance)
        }
    }

    fun handleEvent(event: SearchProductEvent) {
        launch {
            when (event) {
                SearchProductEvent.Back -> back()
                is SearchProductEvent.Search -> search(event.text)
                SearchProductEvent.ClearAll -> {
                    coreManager.clearAllSearch()
                }

                is SearchProductEvent.Clear -> {
                    coreManager.clearTextSearch(event.text)
                }

                is SearchProductEvent.GiftDetail -> {
                    navigationManager.navigate(
                        NavigationDirections.GiftDetail(
                            event.gift.giftId(),
                            event.gift.qrCode
                        )
                    )
                }

                is SearchProductEvent.ViewAllCoupons -> {
                    navigationManager.navigate(NavigationDirections.Coupons)
                }
            }
        }
    }

    private suspend fun search(text: String) {
        val currentUiValue = _uiStateFlow.value.data
        coreManager.insertSearchKey(text)
        _uiStateFlow.emit(UiState(loading = true, data = currentUiValue))
        val gifts = coreManager.getGifts(text).getOrDefault(emptyList())
        _uiStateFlow.emit(UiState(data = currentUiValue!!.copy(gifts = gifts)))
    }
}

data class SearchGiftUiData(
    val gifts: List<Gift> = emptyList(),
    val myBalance: ExchangeBalance = ExchangeBalance(),
)