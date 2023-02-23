package com.noljanolja.android.common.user.data.model

@kotlinx.serialization.Serializable
data class UserRemoteModel(
    val id: String,
    val name: String,
    val profileImage: String,
    val pushNotiEnabled: Boolean,
    val pushToken: String,
)
