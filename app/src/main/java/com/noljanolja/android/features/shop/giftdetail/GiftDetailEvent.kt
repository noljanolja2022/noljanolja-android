package com.noljanolja.android.features.shop.giftdetail

sealed interface GiftDetailEvent {
    object Back : GiftDetailEvent
    object Purchase : GiftDetailEvent
}