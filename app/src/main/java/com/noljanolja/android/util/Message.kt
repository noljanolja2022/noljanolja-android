package com.noljanolja.android.util

import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType

fun Message.canShowArrow(): Boolean = when (type) {
    MessageType.PHOTO, MessageType.DOCUMENT, MessageType.PLAINTEXT -> true
    else -> false
}