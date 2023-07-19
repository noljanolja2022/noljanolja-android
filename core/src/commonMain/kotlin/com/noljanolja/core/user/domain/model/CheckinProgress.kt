package com.noljanolja.core.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckinProgress(
    val id: Long,
    val day: Long,
    val rewardPoints: Long,
    val isCompleted: Boolean,
)