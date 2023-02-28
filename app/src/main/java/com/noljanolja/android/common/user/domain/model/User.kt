package com.noljanolja.android.common.user.domain.model

import kotlinx.serialization.Serializable

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val image: String,
    val phone: String?,
    val gender: Gender? = null,
)

@Serializable
enum class Gender {
    Male, Female, Other
}

fun User?.displayIdentity() = this?.run { name ?: email ?: phone } ?: "Anonymous"
