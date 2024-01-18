package com.noljanolja.android.features.home.wallet.model

import com.noljanolja.core.loyalty.domain.model.*
import kotlinx.datetime.*
import kotlinx.serialization.*

@Serializable
data class UiLoyaltyPoint(
    val id: String = "",
    val status: Status = Status.COMPLETED,
    val amount: Long = 0,
    val reason: String = "",
    val reasonLocale: String = "",
    val unit: String = "",
    val createdAt: Instant = Clock.System.now(),
    val type: Type = Type.RECEIVE,
    val log: String = ""
) : java.io.Serializable {
    fun getPoint() = "+".takeIf { amount > 0 }.orEmpty() + amount
}

enum class Status {
    COMPLETED,
    FAILLED
}

enum class Type {
    RECEIVE,
    SPENT,
}

fun LoyaltyPoint.toUiModel() = UiLoyaltyPoint(
    id = id,
    amount = amount,
    status = when (status) {
        LoyaltyStatus.COMPLETED -> Status.COMPLETED
        LoyaltyStatus.FAILLED -> Status.FAILLED
    },
    reason = reason,
    reasonLocale = reasonLocale,
    unit = unit,
    createdAt = createdAt,
    log = log,
    type = when (type) {
        LoyaltyType.RECEIVE -> Type.RECEIVE
        LoyaltyType.SPENT -> Type.SPENT
    }
)