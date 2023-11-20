package com.noljanolja.android.features.shop.main

sealed interface ShopEvent {
    object Search : ShopEvent

    data class GiftDetail(val giftId: String, val code: String) : ShopEvent

    data class ViewGiftType(val categoryId: String, val categoryName: String) : ShopEvent

    object ViewAllCoupons : ShopEvent

    object Refresh : ShopEvent
}