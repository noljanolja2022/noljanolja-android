package com.noljanolja.android.features.shop.main

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.commons.*
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.data.model.request.*
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

                is ShopEvent.ViewGiftType -> {
                    if (event.categoryId.isNotBlank()) {
                        navigationManager.navigate(
                            NavigationDirections.ProductByCategory(
                                event.categoryId,
                                event.categoryName
                            )
                        )
                    }
                }

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
            val topFeatureGifts = coreManager.getGifts(isFeatured = true).getOrDefault(emptyList())
            val todayOfferGifts =
                coreManager.getGifts(isTodayOffer = true).getOrDefault(emptyList())
            val myGifts = coreManager.getMyGifts().getOrDefault(emptyList())
            val myBalance = coreManager.getExchangeBalance().getOrDefault(ExchangeBalance())
            val brands = coreManager.getBrands(
                GetItemChooseRequest(
                    page = 1,
                    pageSize = 100,
                    query = null
                )
            ).getOrDefault(emptyList())
            val categories = coreManager.getCategories(
                GetItemChooseRequest(
                    page = 1,
                    pageSize = 100,
                    query = null
                )
            ).getOrDefault(emptyList())

            _uiStateFlow.emit(
                UiState(
                    data = ShopUiData(
                        gifts = gifts,
                        topFeatureGifts = topFeatureGifts,
                        myGifts = myGifts,
                        todayOfferGift = todayOfferGifts,
                        myBalance = myBalance,
                        brands = brands ?: emptyList(),
                        category = convertToCategoriesList(categories?.toMutableList())
                    )
                )
            )
        }
    }

    private fun convertToCategoriesList(oldList: List<ItemChoose>?): MutableList<ItemChoose> {
        val newList = mutableListOf(
            ItemChoose(
                name = "All",
                isSelected = true
            )
        )
        oldList?.let(newList::addAll)
        return newList
    }
}

data class ShopUiData(
    val myBalance: ExchangeBalance = ExchangeBalance(),
    val gifts: List<Gift> = emptyList(),
    val topFeatureGifts: List<Gift> = emptyList(),
    val todayOfferGift: List<Gift> = emptyList(),
    val myGifts: List<Gift> = emptyList(),
    val brands: List<ItemChoose> = mutableListOf(),
    val category: MutableList<ItemChoose> = mutableListOf()
)