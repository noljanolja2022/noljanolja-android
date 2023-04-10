package com.noljanolja.core.utils

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.user.domain.model.User

fun User.isAdminOfConversation(conversation: Conversation?): Boolean {
    return conversation?.admin?.id?.takeIf { it.isNotBlank() } == id
}