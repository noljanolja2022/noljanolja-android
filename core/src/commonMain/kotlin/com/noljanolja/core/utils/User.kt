package com.noljanolja.core.utils

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.user.data.model.response.UserRemoteModel
import com.noljanolja.core.user.domain.model.User

fun UserRemoteModel.toDomainUser() = User(
    id = id,
    name = name,
    email = email?.takeIf { it.isNotBlank() },
    avatar = avatar,
    phone = phone?.takeIf { it.isNotBlank() },
    gender = gender,
    referralCode = referralCode,
)

fun User.isRemoveFromConversation(conversation: Conversation): Boolean {
    return conversation.messages.firstOrNull()?.let {
        it.type == MessageType.EVENT_LEFT && it.leftParticipants.any { it.id == id }
    } ?: false
}