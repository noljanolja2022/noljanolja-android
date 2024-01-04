package com.noljanolja.core.contacts.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/4/2024.
 */

@Serializable
data class PointConfig(
    val refereePoints: Long = -1,
    val refererPoints: Long = -1,
    val updatedAt: String = ""
)
