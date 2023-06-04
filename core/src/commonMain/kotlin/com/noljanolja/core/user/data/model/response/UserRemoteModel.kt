package com.noljanolja.core.user.data.model.response

@kotlinx.serialization.Serializable
data class UserRemoteModel(
    val id: String,
    val name: String = "",
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
)
