package com.noljanolja.core.shop.domain.model

import kotlinx.serialization.*

/**
 * Created by tuyen.dang on 1/12/2024.
 */

@Serializable
data class IndiaVoucher(
    @SerialName("voucher_ref")
    var voucher_ref: String = "",
    @SerialName("voucher_settlement_ref")
    var voucherSettlementRef: String = "",
    @SerialName("voucher_name")
    var voucherName: String = "",
    var status: String = "",
    @SerialName("expiration_date")
    var expirationDate: String = "",
    @SerialName("edenred_url")
    var edenredUrl: String = "",
    @SerialName("view_code")
    var viewCode: String = "",
    @SerialName("pin_code")
    var pinCode: String = "",
    @SerialName("display_codes")
    var displayCodes: List<String> = emptyList(),
)
