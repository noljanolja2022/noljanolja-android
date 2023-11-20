package com.noljanolja.android.features.shop.main

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.commons.*
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
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

                is ShopEvent.ViewGiftType -> navigationManager.navigate(
                    NavigationDirections.ProductByCategory(event.categoryId, event.categoryName)
                )

                ShopEvent.ViewAllCoupons -> navigationManager.navigate(NavigationDirections.Coupons)

                ShopEvent.Refresh -> {
                    delay(200)
                    refresh()
                }
            }
        }
    }

    fun refresh() {
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
            val myBalance = coreManager.getExchangeBalance().getOrDefault(ExchangeBalance())
            _uiStateFlow.emit(
                UiState(
                    data = ShopUiData(
                        gifts = gifts,
                        myGifts = myGifts,
                        myBalance = myBalance,
                        category = mutableListOf(
                            ItemChoose(
                                id = "1",
                                image = "1",
                                name = "All",
                                isSelected = true
                            ),
                            ItemChoose(
                                id = "2",
                                image = "2",
                                name = "Food & Drink"
                            ),
                            ItemChoose(
                                id = "3",
                                image = "123",
                                name = "Decor"
                            ),
                            ItemChoose(
                                id = "4",
                                image = "123",
                                name = "Fashion Clothes"
                            )
                        )
                    )
                )
            )
        }
    }
}

data class ShopUiData(
    val myBalance: ExchangeBalance = ExchangeBalance(),
    val gifts: List<Gift> = emptyList(),
    val myGifts: List<Gift> = emptyList(),
    val category: MutableList<ItemChoose> = mutableListOf()
)