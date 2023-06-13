package com.noljanolja.android.features.shop.main

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ShopViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<ShopUiData>>(UiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    init {
        refresh()
    }

    fun handleEvent(event: ShopEvent) {
        launch {
            when (event) {
                ShopEvent.Search -> navigationManager.navigate(NavigationDirections.SearchProduct)
                is ShopEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code)
                )

                ShopEvent.ViewAllCoupons -> navigationManager.navigate(NavigationDirections.Coupons)
                ShopEvent.Refresh -> {
                    delay(200)
                    refresh()
                }
            }
        }
    }

    private fun refresh() {
        launch {
            val currentData = _uiStateFlow.value
            _uiStateFlow.emit(
                UiState(
                    loading = true,
                    data = currentData.data
                )
            )
            val gifts = coreManager.getGifts().getOrDefault(emptyList())
            val myGifts = coreManager.getMyGifts().getOrDefault(emptyList())
            _uiStateFlow.emit(
                UiState(
                    data = ShopUiData(gifts = gifts, myGifts = myGifts)
                )
            )
        }
    }
}

data class ShopUiData(
    val gifts: List<Gift> = emptyList(),
    val myGifts: List<Gift> = emptyList(),
)