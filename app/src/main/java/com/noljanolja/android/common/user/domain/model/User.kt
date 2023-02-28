package com.noljanolja.android.common.user.domain.model

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val image: String,
    val phone: String?,
)

fun User?.displayIdentity() = this?.run { name ?: email ?: phone } ?: "Anonymous"
