package com.noljanolja.core.contacts.domain.model

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/14/2024.
 */

@Serializable
data class NotificationData(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val type: String = "",
    val body: String = "",
    val image: String = "",
    val data: String = "",
    var timeDisplay: Long? = null,
    val isRead: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)
