package com.noljanolja.android.util

import com.noljanolja.android.common.user.data.model.UserRemoteModel
import com.noljanolja.android.common.user.domain.model.User

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name,
    email = "email",
    image = profileImage,
)