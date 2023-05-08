package com.noljanolja.android.util

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.utils.isNormalType

fun Conversation.isSeen(): Boolean {
    return this.messages.firstOrNull { m -> m.isNormalType() }?.isSeenByMe ?: true
}