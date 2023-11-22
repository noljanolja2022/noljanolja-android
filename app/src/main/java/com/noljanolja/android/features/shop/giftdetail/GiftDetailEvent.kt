package com.noljanolja.android.features.shop.giftdetail

sealed interface GiftDetailEvent {
    object Back : GiftDetailEvent

    object Purchase : GiftDetailEvent

    data class GiftDetail(val giftId: String, val code: String) : GiftDetailEvent
}