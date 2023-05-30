package com.noljanolja.core.user.data.model.request

import com.noljanolja.core.user.domain.model.Gender

@kotlinx.serialization.Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val gender: Gender? = null,
    val dob: String? = null,
    val preferences: Preferences? = null,
) {
    @kotlinx.serialization.Serializable
    data class Preferences(
        val collectAndUsePersonalInfo: Boolean? = null,
    )
}

data class UpdateAvatarRequest(
    val name: String,
    val type: String,
    val files: ByteArray,
)