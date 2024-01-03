package com.noljanolja.core.user.data.model.request

import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/3/2024.
 */

@Serializable
data class SendPointRequest(
    val isRequestPoint: Boolean,
    val toUserId: String,
    val points: Long
)
