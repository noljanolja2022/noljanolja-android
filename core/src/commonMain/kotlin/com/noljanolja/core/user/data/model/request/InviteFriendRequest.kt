package com.noljanolja.core.user.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class InviteFriendRequest(
    val friendId: String
)