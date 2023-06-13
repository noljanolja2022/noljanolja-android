package com.noljanolja.android.features.shop.main

sealed interface ShopEvent {
    object Search : ShopEvent
    data class GiftDetail(val giftId: Long, val code: String) : ShopEvent
    object ViewAllCoupons : ShopEvent
    object Refresh : ShopEvent
}