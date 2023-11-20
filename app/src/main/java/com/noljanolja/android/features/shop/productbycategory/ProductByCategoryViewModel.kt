package com.noljanolja.android.features.shop.productbycategory

import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 11/20/2023.
 */

class ProductByCategoryViewModel(
    private val categoryId: String = ""
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(
        UiState<MutableList<Gift>>(
            loading = true,
            data = null
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        getProductByCategory()
    }

    fun handleEvent(event: ProductByCategoryEvent) {
        launch {
            when (event) {
                ProductByCategoryEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)

                is ProductByCategoryEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code)
                )
            }
        }
    }

    private fun getProductByCategory() {
        launch {
            val gifts = coreManager.getGifts(categoryId = categoryId).getOrDefault(emptyList())
            _uiStateFlow.emit(
                UiState(
                    loading = false,
                    data = gifts.toMutableList()
                )
            )
        }
    }
}
