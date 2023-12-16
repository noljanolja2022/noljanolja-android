package com.noljanolja.core.shop.data.model.request

/**
 * Created by tuyen.dang on 11/29/2023.
 */

data class GetGiftListRequest(
    val searchText: String = "",
    val categoryId: String = "",
    val brandId: String = "",
    val locale: String,
    val isFeatured: Boolean? = null,
    val isTodayOffer: Boolean? = null,
    val isRecommended: Boolean? = null,
)
