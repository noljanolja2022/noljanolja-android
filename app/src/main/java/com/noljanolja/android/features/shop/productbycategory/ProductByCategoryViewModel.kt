package com.noljanolja.android.features.shop.productbycategory

import com.noljanolja.android.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 11/20/2023.
 */

class ProductByCategoryViewModel() : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(
        UiState<MutableList<Gift>>(
            loading = true,
            data = null
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: ProductByCategoryEvent) {
        launch {
            when (event) {
                ProductByCategoryEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)

                is ProductByCategoryEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code, event.log)
                )
            }
        }
    }

    internal fun getProductByCategoryOrBrand(
        categoryId: String,
        brandId: String
    ) {
        launch {
            val gifts = coreManager.getGifts(
                GetGiftListRequest(
                    categoryId = categoryId,
                    brandId = brandId,
                    locale = MyApplication.localeSystem
                )
            ).getOrDefault(emptyList())
            _uiStateFlow.emit(
                UiState(
                    loading = false,
                    data = gifts.toMutableList()
                )
            )
        }
    }
}
