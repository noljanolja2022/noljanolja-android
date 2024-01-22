package com.noljanolja.core.user.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val gender: Gender? = null,
    val dob: LocalDate? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    var isMe: Boolean = false,
    val referralCode: String = "",
) {
    fun getAvatarUrl() = avatar?.replace("storage", "storage-download")
    fun getQrUrl() = "NolgoBulja:id:$id"
    fun compareUser(anotherUser: User) =
        (name.contains(anotherUser.name) || id == anotherUser.id
                || email?.contains(anotherUser.email.toString()) == true
                || phone?.contains(anotherUser.phone.toString()) == true)
}

@Serializable
enum class Gender {
    MALE, FEMALE, OTHER
}

fun User?.displayIdentity() = this?.name ?: "Anonymous"
