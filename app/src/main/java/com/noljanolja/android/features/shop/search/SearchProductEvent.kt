package com.noljanolja.android.features.shop.search

import com.noljanolja.core.shop.domain.model.Gift

sealed interface SearchProductEvent {
    object Back : SearchProductEvent
    data class Search(val text: String) : SearchProductEvent
    object ClearAll : SearchProductEvent
    data class Clear(val text: String) : SearchProductEvent
    data class GiftDetail(val gift: Gift) : SearchProductEvent
    object ViewAllCoupons : SearchProductEvent
}