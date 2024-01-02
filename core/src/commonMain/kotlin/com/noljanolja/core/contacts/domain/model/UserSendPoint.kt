package com.noljanolja.core.contacts.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/3/2024.
 */

@Serializable
data class UserSendPoint(
    val id: Long = -1,
    val fromUserId: String = "",
    val toUserId: String = "",
    val points: Long = 0,
    val emails: String = "",
    val type: String = "",
    val createdAt: String = "",
)
