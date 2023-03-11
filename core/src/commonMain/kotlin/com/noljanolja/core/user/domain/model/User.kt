package com.noljanolja.core.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val gender: Gender? = null,
) {
    var isMe: Boolean = id == currentUserId
    fun getAvatarUrl() = avatar

    companion object {
        var currentUserId: String? = null
    }
}

@Serializable
enum class Gender {
    MALE, FEMALE, OTHER
}

fun User?.displayIdentity() = this?.name ?: "Anonymous"
