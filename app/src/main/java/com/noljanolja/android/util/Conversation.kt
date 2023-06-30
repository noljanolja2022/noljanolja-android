package com.noljanolja.android.util

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageReaction
import com.noljanolja.core.conversation.domain.model.ReactIcon
import com.noljanolja.core.utils.isNormalType

fun Conversation.isSeen(): Boolean {
    return this.messages.firstOrNull { m -> m.isNormalType() }?.isSeenByMe ?: true
}

fun Message.getDefaultReaction(
    reactIcons: List<ReactIcon>,
): ReactIcon? {
    val reactions = reactions.reversed()
    return reactions.firstOrNull()?.let {
        ReactIcon(
            id = it.reactionId,
            code = it.reactionCode,
            description = it.reactionDescription
        )
    } ?: reactIcons.firstOrNull()?.copy(code = "\uD83E\uDD0D")
        .takeIf { !sender.isMe }
}

fun Message.getDisplayReactions(showReaction: Boolean = true): List<MessageReaction>? {
    return reactions.reversed().distinctBy { it.reactionCode }.take(3)
        .takeIf { it.isNotEmpty() && showReaction }
}