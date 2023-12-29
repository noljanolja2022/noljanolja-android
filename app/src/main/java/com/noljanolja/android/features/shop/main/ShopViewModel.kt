package com.noljanolja.android.features.shop.main

import androidx.lifecycle.*
import com.noljanolja.android.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.extensions.*
import com.noljanolja.core.commons.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ShopViewModel : BaseViewModel() {
    companion object {
        const val KEY_GIFTS = "gifts"
        const val KEY_TODAY_FEATURE_GIFTS = "topFeatureGifts"
        const val KEY_TODAY_OFFERS_GIFTS = "todayOfferGifts"
        const val KEY_RECOMMENDS_GIFTS = "recommendsGift"
        const val KEY_MY_GIFTS = "myGifts"
        const val KEY_MY_BALANCE = "myBalance"
        const val KEY_BRANDS = "brands"
        const val KEY_CATEGORIES = "categories"
    }

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

                ShopEvent.Setting -> navigationManager.navigate(NavigationDirections.Setting)

                is ShopEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code)
                )

                is ShopEvent.ViewGiftType -> {
                    event.run {
                        if (categoryId.isNotBlank() || brandId.isNotBlank()) {
                            navigationManager.navigate(
                                NavigationDirections.ProductByCategory(
                                    brandId = brandId,
                                    categoryId = categoryId,
                                    categoryName = categoryName
                                )
                            )
                        }
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
            val gifts = mutableListOf<Gift>()
            val topFeatureGifts = mutableListOf<Gift>()
            val todayOfferGifts = mutableListOf<Gift>()
            val recommendsGift = mutableListOf<Gift>()
            val myGifts = mutableListOf<Gift>()
            var myBalance = ExchangeBalance()
            val brands = mutableListOf<ItemChoose>()
            val categories = mutableListOf<ItemChoose>()
            val requests =
                coreManager.run {
                    listOf(
                        BaseFunCallAPI(
                            key = KEY_GIFTS,
                            funCallAPI = {
                                getGifts(
                                    GetGiftListRequest(
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                        BaseFunCallAPI(
                            key = KEY_TODAY_FEATURE_GIFTS,
                            funCallAPI = {
                                getGifts(
                                    GetGiftListRequest(
                                        isFeatured = true,
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                        BaseFunCallAPI(
                            key = KEY_TODAY_OFFERS_GIFTS,
                            funCallAPI = {
                                getGifts(
                                    GetGiftListRequest(
                                        isTodayOffer = true,
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                        BaseFunCallAPI(
                            key = KEY_RECOMMENDS_GIFTS,
                            funCallAPI = {
                                getGifts(
                                    GetGiftListRequest(
                                        isRecommended = true,
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                        BaseFunCallAPI(
                            key = KEY_MY_GIFTS,
                            funCallAPI = ::getMyGifts
                        ),
                        BaseFunCallAPI(
                            key = KEY_MY_BALANCE,
                            funCallAPI = ::getExchangeBalance
                        ),
                        BaseFunCallAPI(
                            key = KEY_BRANDS,
                            funCallAPI = {
                                getBrands(
                                    GetItemChooseRequest(
                                        page = 1,
                                        pageSize = 100,
                                        query = null,
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                        BaseFunCallAPI(
                            key = KEY_CATEGORIES,
                            funCallAPI = {
                                getCategories(
                                    GetItemChooseRequest(
                                        page = 1,
                                        pageSize = 100,
                                        query = null,
                                        locale = MyApplication.localeSystem
                                    )
                                )
                            }
                        ),
                    )
                }
            callMultipleApisOnThread(
                requests = requests,
                onEachSuccess = { data, key ->
                    when (key) {
                        KEY_GIFTS -> {
                            (data.castTo<MutableList<Gift>>())?.let {
                                gifts.addAll(it)
                            }
                        }

                        KEY_TODAY_FEATURE_GIFTS -> {
                            (data.castTo<MutableList<Gift>>())?.let {
                                topFeatureGifts.addAll(it)
                            }
                        }

                        KEY_TODAY_OFFERS_GIFTS -> {
                            (data.castTo<MutableList<Gift>>())?.let {
                                todayOfferGifts.addAll(it)
                            }
                        }

                        KEY_RECOMMENDS_GIFTS -> {
                            (data.castTo<MutableList<Gift>>())?.let {
                                recommendsGift.addAll(it)
                            }
                        }

                        KEY_MY_GIFTS -> {
                            (data.castTo<MutableList<Gift>>())?.let {
                                myGifts.addAll(it)
                            }
                        }

                        KEY_MY_BALANCE -> {
                            (data.castTo<ExchangeBalance>())?.let {
                                myBalance = it
                            }
                        }

                        KEY_BRANDS -> {
                            (data.castTo<MutableList<ItemChoose>>())?.let {
                                brands.addAll(it)
                            }
                        }

                        KEY_CATEGORIES -> {
                            (data.castTo<MutableList<ItemChoose>>())?.let {
                                categories.addAll(it)
                            }
                        }
                    }
                },
                onFinish = {
                    _uiStateFlow.emit(
                        UiState(
                            data = ShopUiData(
                                gifts = gifts,
                                topFeatureGifts = topFeatureGifts,
                                myGifts = myGifts,
                                todayOfferGift = todayOfferGifts,
                                recommendsGift = recommendsGift,
                                myBalance = myBalance,
                                brands = brands,
                                category = convertToCategoriesList(categories.toMutableList())
                            )
                        )
                    )
                }
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
    val recommendsGift: List<Gift> = emptyList(),
    val myGifts: List<Gift> = emptyList(),
    val brands: List<ItemChoose> = mutableListOf(),
    val category: MutableList<ItemChoose> = mutableListOf()
)