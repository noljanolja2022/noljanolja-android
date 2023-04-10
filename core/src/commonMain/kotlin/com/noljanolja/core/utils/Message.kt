package com.noljanolja.core.utils

import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType

fun Message.isNormalType() = when (type) {
    MessageType.EVENT_LEFT, MessageType.EVENT_JOINED, MessageType.EVENT_UPDATED -> false
    else -> true
}