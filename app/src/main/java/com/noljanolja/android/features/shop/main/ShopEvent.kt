package com.noljanolja.android.features.shop.main

sealed interface ShopEvent {
    object Search : ShopEvent

    object Setting : ShopEvent

    data class GiftDetail(
        val giftId: String,
        val code: String,
        val log: String?
    ) : ShopEvent

    data class ViewGiftType(
        val brandId: String = "",
        val categoryId: String = "",
        val categoryName: String
    ) : ShopEvent

    object ViewAllCoupons : ShopEvent

    object Refresh : ShopEvent
}