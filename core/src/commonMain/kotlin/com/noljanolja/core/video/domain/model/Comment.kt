package com.noljanolja.core.video.domain.model

import com.noljanolja.core.utils.randomUUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Long = 0,
    @SerialName("localId")
    private val _localId: String = randomUUID(),
    val comment: String = "",
    val commenter: Commenter = Commenter(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    val localId: String get() = _localId.takeIf { it.isNotBlank() } ?: randomUUID()
}