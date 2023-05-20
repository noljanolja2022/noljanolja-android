package com.noljanolja.android.features.home.wallet.model

import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import com.noljanolja.core.loyalty.domain.model.LoyaltyStatus
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UiLoyaltyPoint(
    val id: String = "",
    val status: Status = Status.COMPLETE,
    val amount: Long = 0,
    val reason: String = "",
    val createdAt: Instant = Clock.System.now(),
    val type: Type = Type.RECEIVE,
) : java.io.Serializable {
    fun getPoint() = "+".takeIf { amount > 0 }.orEmpty() + amount
}

enum class Status {
    COMPLETE,
}

enum class Type {
    RECEIVE,
    SPENT,
}

fun LoyaltyPoint.toUiModel() = UiLoyaltyPoint(
    id = id,
    amount = amount,
    status = when (status) {
        LoyaltyStatus.COMPLETE -> Status.COMPLETE
    },
    reason = reason,
    createdAt = createdAt,
    type = when (type) {
        LoyaltyType.RECEIVE -> Type.RECEIVE
        LoyaltyType.SPENT -> Type.SPENT
    }
)