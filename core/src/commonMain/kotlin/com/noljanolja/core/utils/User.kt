package com.noljanolja.core.utils

import com.noljanolja.core.user.data.model.response.UserRemoteModel
import com.noljanolja.core.user.domain.model.User

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name,
    email = email?.takeIf { it.isNotBlank() },
    avatar = avatar,
    phone = phone?.takeIf { it.isNotBlank() },
)