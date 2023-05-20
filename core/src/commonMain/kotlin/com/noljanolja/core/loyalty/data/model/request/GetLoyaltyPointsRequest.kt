package com.noljanolja.core.loyalty.data.model.request

data class GetLoyaltyPointsRequest(
    val type: FilterType,
    val month: Int?,
    val year: Int?,
) {
    enum class FilterType {
        ALL, RECEIVE, SPENT
    }
}