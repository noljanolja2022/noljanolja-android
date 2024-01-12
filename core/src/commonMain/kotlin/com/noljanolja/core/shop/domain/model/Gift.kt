package com.noljanolja.core.shop.domain.model

import com.noljanolja.core.commons.*
import kotlinx.serialization.Serializable

@Serializable
data class Gift(
    val brand: ItemChoose = ItemChoose(),
    val description: String = "",
    val endTime: String = "",
    val giftNo: Int = 0,
    val id: String = "",
    val giftId: String = "",
    val image: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val qrCode: String = "",
    val category: ItemChoose = ItemChoose(),
    val log: String? = ""
) {
    fun giftId() = giftId.takeIf { it.isNotBlank() } ?: id

    fun isPurchased(isCheckInDetail: Boolean = false) =
        qrCode.isNotBlank() || (price == 0.0 || isCheckInDetail && !log.isNullOrBlank())
}
