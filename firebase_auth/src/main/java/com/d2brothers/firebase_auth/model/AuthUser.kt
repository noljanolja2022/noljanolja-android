package com.d2brothers.firebase_auth.model

data class AuthUser(
    val email: String?,
    val displayName: String?,
    val isVerify: Boolean,
)

fun AuthUser?.displayIdentity() = this?.displayName ?: this?.email ?: "Anonymous"
