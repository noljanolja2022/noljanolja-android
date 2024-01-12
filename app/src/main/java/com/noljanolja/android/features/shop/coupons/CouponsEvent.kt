package com.noljanolja.android.features.shop.coupons

sealed interface CouponsEvent {
    object Back : CouponsEvent
    data class GiftDetail(
        val giftId: String,
        val code: String,
        val log: String?
    ) : CouponsEvent
}