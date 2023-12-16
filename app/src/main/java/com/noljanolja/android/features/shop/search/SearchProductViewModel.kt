package com.noljanolja.android.features.shop.search

import androidx.lifecycle.*
import com.noljanolja.android.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.flow.*

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
        val gifts = coreManager.getGifts(
            GetGiftListRequest(
                searchText = text,
                locale = MyApplication.localeSystem
            )
        ).getOrDefault(emptyList())
        _uiStateFlow.emit(UiState(data = currentUiValue!!.copy(gifts = gifts)))
    }
}

data class SearchGiftUiData(
    val gifts: List<Gift> = emptyList(),
    val myBalance: ExchangeBalance = ExchangeBalance(),
)