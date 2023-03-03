package com.noljanolja.android.common.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val image: String = "",
    val phone: String? = null,
    val gender: Gender? = null,
    var isMe: Boolean = false,
) {
    fun getAvatarUrl() = image
}

@Serializable
enum class Gender {
    Male, Female, Other
}

fun User?.displayIdentity() = this?.run { name ?: email ?: phone } ?: "Anonymous"
