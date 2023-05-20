package com.noljanolja.core.loyalty.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class LoyaltyPoint(
    val id: String = "",
    val status: LoyaltyStatus = LoyaltyStatus.Complete,
    val amount: Long = Random.nextLong(-10000, 10000L),
    val reason: String = randomString(Random.nextInt(10, 20)),
    val createdAt: Instant = Clock.System.now(),
) {
    val type: LoyaltyType = if (amount >= 0) LoyaltyType.Receive else LoyaltyType.Spent
}

enum class LoyaltyType {
    Receive,
    Spent,
}

enum class LoyaltyStatus {
    Complete,
}

fun randomString(length: Int): String {
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("") + "\${}\"\'"
}

// Sử dụng hàm randomString để tạo ra một chuỗi ngẫu nhiên với độ dài là 10
val randomStr = randomString(10)
