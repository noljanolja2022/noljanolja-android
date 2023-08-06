package com.noljanolja.core.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckinProgress(
    val id: Long = 0L,
    val day: String = "",
    val rewardPoints: Long = 0L,
) {
    val isCompleted = rewardPoints > 0
}