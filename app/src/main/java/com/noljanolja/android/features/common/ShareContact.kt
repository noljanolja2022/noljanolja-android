package com.noljanolja.android.features.common

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.user.domain.model.User

class ShareContact(
    val conversationId: Long? = null,
    val userId: String? = null,
    val title: String = "",
    val avatar: String? = null,
)

fun Conversation.toShareContact() = ShareContact(
    conversationId = id,
    userId = participants.firstOrNull { !it.isMe }
        .takeIf { type == ConversationType.SINGLE }?.id,
    title = getDisplayTitle(),
    avatar = getDisplayAvatarUrl()
)

fun User.toShareContact() = ShareContact(
    userId = id,
    title = name,
    avatar = avatar
)