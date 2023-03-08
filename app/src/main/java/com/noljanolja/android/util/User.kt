package com.noljanolja.android.util

import com.noljanolja.android.common.user.domain.model.response.UserRemoteModel
import com.noljanolja.android.common.user.domain.model.User

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name.takeIf { it.isNotBlank() },
    email = email.takeIf { it.isNotBlank() },
    image = avatar,
    phone = phone.takeIf { it.isNotBlank() },
)