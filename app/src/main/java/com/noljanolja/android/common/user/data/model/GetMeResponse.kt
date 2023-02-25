package com.noljanolja.android.common.user.data.model

@kotlinx.serialization.Serializable
data class GetMeResponse(
    val code: Int,
    val message: String,
    val data: UserRemoteModel,
)
