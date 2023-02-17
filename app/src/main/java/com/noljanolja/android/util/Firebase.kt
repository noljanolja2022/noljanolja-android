package com.noljanolja.android.util

import com.google.firebase.auth.FirebaseUser
import com.noljanolja.android.common.auth.domain.model.User

fun FirebaseUser?.toDomainUser() = this?.let { user ->
    User(
        id = user.uid,
        isVerify = user.providerData.all { it.providerId != "password" } || user.isEmailVerified,
    )
}
