package com.noljanolja.android.common.user.domain.model.response

@kotlinx.serialization.Serializable
data class UserRemoteModel(
    val id: String,
    val name: String,
    val avatar: String,
    val phone: String,
    val email: String,
    val isEmailVerified: Boolean,
)
