package com.noljanolja.android.features.shop.main

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.MemberInfo

class ShopViewModel : BaseViewModel() {
    fun handleEvent(event: ShopEvent) {
        launch {
            when (event) {
                ShopEvent.Search -> navigationManager.navigate(NavigationDirections.SearchProduct)
                ShopEvent.GiftDetail -> navigationManager.navigate(NavigationDirections.GiftDetail)
            }
        }
    }
}

data class ShopUiData(
    val memberInfo: MemberInfo,
)