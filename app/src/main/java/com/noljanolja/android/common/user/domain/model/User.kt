package com.noljanolja.android.common.user.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String?,
    val image: String?,
)

fun User?.displayIdentity() = this?.name ?: this?.email ?: "Anonymous"
