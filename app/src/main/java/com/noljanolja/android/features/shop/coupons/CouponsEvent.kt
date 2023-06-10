package com.noljanolja.android.features.shop.coupons

sealed interface CouponsEvent {
    object Back : CouponsEvent
}