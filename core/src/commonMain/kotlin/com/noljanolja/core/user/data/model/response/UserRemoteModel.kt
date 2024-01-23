package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.user.domain.model.*

@kotlinx.serialization.Serializable
data class UserRemoteModel(
    val id: String,
    val name: String = "",
    val avatar: String? = null,
    val phone: String? = null,
    val gender: Gender? = null,
    val email: String? = null,
    val referralCode: String = "",
)
