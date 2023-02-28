package com.noljanolja.android.util

import com.noljanolja.android.common.user.data.model.UserRemoteModel
import com.noljanolja.android.common.user.domain.model.User

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name.takeIf { it.isNotBlank() },
    email = email.takeIf { it.isNotBlank() },
    image = profileImage,
    phone = phone.takeIf { it.isNotBlank() },
)