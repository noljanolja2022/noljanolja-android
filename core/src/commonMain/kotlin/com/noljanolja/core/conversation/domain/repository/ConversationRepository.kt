package com.noljanolja.core.conversation.domain.repository

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun findConversationWithUser(userId: String): Conversation?

    suspend fun getConversation(
        conversationId: Long,
    ): Flow<Conversation>

    suspend fun getConversations(): Flow<List<Conversation>>

    suspend fun sendConversationMessage(
        conversationId: Long,
        userId: String,
        message: Message,
    ): Long

    suspend fun getConversationMessages(
        conversationId: Long,
        messageBefore: Long? = null,
        messageAfter: Long? = null,
    ): List<Message>
}