package com.noljanolja.android.common.user.data.model

import com.noljanolja.android.common.user.domain.model.User

@kotlinx.serialization.Serializable
data class GetMeResponse(
    val code: Int,
    val message: String,
    val data: UserRemoteModel,
)

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name,
    email = "email",
    image = profileImage,
)
