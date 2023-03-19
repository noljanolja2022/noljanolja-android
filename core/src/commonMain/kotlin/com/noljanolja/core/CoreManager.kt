package com.noljanolja.core

import co.touchlab.kermit.Logger
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CoreManager : KoinComponent {
    private val contactsRepository: ContactsRepository by inject()
    private val userRepository: UserRepository by inject()
    private val conversationRepository: ConversationRepository by inject()
    private val authRepository: AuthRepository by inject()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            authRepository.getAuthToken()
                .catch {
                    Logger.e(it) { "Collect token error ${it.message}" }
                }
                .collect {
                    streamConversation()
                }
        }
    }

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

    private suspend fun streamConversation() {
        conversationRepository.streamConversations()
    }

    suspend fun updateMessageStatus(conversationId: Long, messageId: Long) {
        conversationRepository.updateMessageStatus(conversationId, messageId)
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
        return userRepository.logout()
    }

    suspend fun getAuthToken(): String? = authRepository.getAuthToken().firstOrNull()

    suspend fun saveAuthToken(
        authToken: String,
    ) {
        authRepository.saveAuthToken(authToken)
    }

    suspend fun pushToken() {
        authRepository.getPushToken()?.let {
            userRepository.pushTokens(it)
        }
    }

    suspend fun delete() = authRepository.delete()

    fun onDestroy() {
        scope.coroutineContext.cancel()
    }
}