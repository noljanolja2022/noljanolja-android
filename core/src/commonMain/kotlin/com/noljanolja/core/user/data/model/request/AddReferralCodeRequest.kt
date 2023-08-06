package com.noljanolja.core.user.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AddReferralCodeRequest(
    val referredByCode: String,
)