package com.noljanolja.core.shop.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Gift(
    val brand: Brand = Brand(),
    val description: String = "",
    val endTime: String = "",
    val giftNo: Int = 0,
    val id: String = "",
    val giftId: String = "",
    val image: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val qrCode: String = "",
) {
    fun giftId() = giftId.takeIf { it.isNotBlank() } ?: id
}

@Serializable
data class Brand(
    val id: String = "",
    val image: String = "",
    val name: String = "",
)