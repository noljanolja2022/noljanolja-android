package com.noljanolja.android.features.shop.main

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShopViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<ShopUiData>>(UiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val memberInfo = coreManager.getMemberInfo().getOrDefault(MemberInfo())
            val gifts = coreManager.getGifts().getOrDefault(emptyList())
            val myGifts = coreManager.getMyGifts().getOrDefault(emptyList())
            _uiStateFlow.emit(
                UiState(
                    data = ShopUiData(memberInfo = memberInfo, gifts = gifts, myGifts = myGifts)
                )
            )
        }
    }

    fun handleEvent(event: ShopEvent) {
        launch {
            when (event) {
                ShopEvent.Search -> navigationManager.navigate(NavigationDirections.SearchProduct)
                is ShopEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId)
                )

                ShopEvent.ViewAllCoupons -> navigationManager.navigate(NavigationDirections.Coupons)
            }
        }
    }
}

data class ShopUiData(
    val memberInfo: MemberInfo,
    val gifts: List<Gift> = emptyList(),
    val myGifts: List<Gift> = emptyList(),
)