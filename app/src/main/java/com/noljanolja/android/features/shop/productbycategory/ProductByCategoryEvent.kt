package com.noljanolja.android.features.shop.productbycategory

/**
 * Created by tuyen.dang on 11/20/2023.
 */

interface ProductByCategoryEvent {
    data class GiftDetail(val giftId: String, val code: String) : ProductByCategoryEvent

    object GoBack : ProductByCategoryEvent
}
