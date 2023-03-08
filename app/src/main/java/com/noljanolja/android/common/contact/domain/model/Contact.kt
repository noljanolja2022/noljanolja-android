package com.noljanolja.android.common.contact.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: Long,
    val name: String,
    val phones: List<String>,
    val emails: List<String>,
) {
    fun getAvatarUrl() = ""
}