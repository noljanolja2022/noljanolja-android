package com.noljanolja.android.util

import com.google.firebase.auth.FirebaseUser
import com.noljanolja.android.common.domain.model.User

fun FirebaseUser?.toDomainUser() = this?.let {
    User(
        id = it.uid
    )
}
