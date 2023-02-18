package com.d2brothers.firebase_auth.utils

import com.d2brothers.firebase_auth.model.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthUser() = AuthUser(
    displayName = displayName,
    email = email,
    isVerify = providerData.all { it.providerId != "password" } || isEmailVerified,
)
