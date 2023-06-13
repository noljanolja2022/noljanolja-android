package com.noljanolja.android.features.shop.coupons

sealed interface CouponsEvent {
    object Back : CouponsEvent
    data class GiftDetail(val giftId: Long, val code: String) : CouponsEvent
}