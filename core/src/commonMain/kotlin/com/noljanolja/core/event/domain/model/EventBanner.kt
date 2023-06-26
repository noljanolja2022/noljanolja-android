package com.noljanolja.core.event.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventBanner(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val isActive: Boolean = true,
    val image: String = "",
    val priority: EventPriority = EventPriority.LOW,
    val action: EventAction = EventAction.NONE,
    val endTime: Instant = Clock.System.now(),
    val startTime: Instant = Clock.System.now(),
)

enum class EventPriority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class EventAction {
    NONE, LINK, SHARE, CHECKIN
}