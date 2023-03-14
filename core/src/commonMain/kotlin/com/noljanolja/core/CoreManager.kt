package com.noljanolja.core

import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class CoreManager(
    private val contactsRepository: ContactsRepository,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository,
) {

    var latestConversationId: Long = 0
    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return contactsRepository.syncUserContacts(contacts)
    }

    suspend fun findConversationWithUser(userId: String): Conversation? {
        return conversationRepository.findConversationWithUser(userId)
    }

    suspend fun getConversation(conversationId: Long): Flow<Conversation> {
        return conversationRepository.getConversation(conversationId)
    }

    suspend fun getConversations(): Flow<List<Conversation>> {
        return conversationRepository.getConversations()
    }

    suspend fun sendConversationMessage(
        conversationId: Long,
        userId: String,
        message: Message,
    ): Long {
        return conversationRepository.sendConversationMessage(
            conversationId = conversationId,
            userId = userId,
            message = message
        )
    }

    suspend fun getConversationMessages(
        conversationId: Long,
        messageBefore: Long?,
        messageAfter: Long?,
    ): List<Message> {
        return conversationRepository.getConversationMessages(
            conversationId = conversationId,
            messageBefore = messageBefore,
            messageAfter = messageAfter
        )
    }

    suspend fun getCurrentUser(forceRefresh: Boolean = false): Result<User> {
        return userRepository.getCurrentUser(forceRefresh)
    }

    suspend fun pushTokens(token: String): Result<Boolean> {
        authRepository.savePushToken(token)
        return userRepository.pushTokens(token)
    }

    suspend fun verifyOTPCode(verificationId: String, otp: String): Result<User> {
        return userRepository.verifyOTPCode(verificationId, otp)
    }

    suspend fun updateUser(name: String, photo: String?): Result<User> {
        return userRepository.updateUser(name, photo)
    }

    suspend fun logout(): Result<Boolean> {
        authRepository.delete()
        return userRepository.logout()
    }

    suspend fun getAuthToken(): String? = authRepository.getAuthToken()

    suspend fun saveAuthToken(
        authToken: String,
    ) = authRepository.saveAuthToken(authToken)

    suspend fun delete() = authRepository.delete()
}