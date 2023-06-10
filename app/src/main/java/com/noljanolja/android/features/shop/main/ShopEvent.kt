package com.noljanolja.android.features.shop.main

sealed interface ShopEvent {
    object Search : ShopEvent
    data class GiftDetail(val giftId: Long) : ShopEvent
    object ViewAllCoupons : ShopEvent
}