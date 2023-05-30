package com.noljanolja.core

import co.touchlab.kermit.Logger
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.domain.model.Contact
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageStatus
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import com.noljanolja.core.loyalty.domain.repository.LoyaltyRepository
import com.noljanolja.core.media.domain.repository.MediaRepository
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.video.data.model.request.VideoProgressEvent
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.repository.VideoRepository
import com.noljanolja.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes

class CoreManager : KoinComponent {
    private val contactsRepository: ContactsRepository by inject()
    private val userRepository: UserRepository by inject()
    private val conversationRepository: ConversationRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val mediaRepository: MediaRepository by inject()
    private val videoRepository: VideoRepository by inject()
    private val loyaltyRepository: LoyaltyRepository by inject()
    private val socketManager: SocketManager by inject()

    private val scope = CoroutineScope(Dispatchers.Default)

    var timeSaveToken: Instant? = null

    fun getRemovedConversationEvent() = conversationRepository.removedConversationEvent

    suspend fun syncUserContacts(contacts: List<Contact>): Result<List<User>> {
        return contactsRepository.syncUserContacts(contacts)
    }

    suspend fun getContacts(page: Int): Result<List<User>> {
        return contactsRepository.getContacts(page)
    }

    suspend fun findContacts(phoneNumber: String): Result<List<User>> {
        return contactsRepository.findContacts(phoneNumber)
    }

    suspend fun findConversationWithUsers(userIds: List<String>): Conversation? {
        return userIds.let {
            conversationRepository.findConversationWithUsers(it)
        }
    }

    suspend fun getConversation(conversationId: Long): Flow<Conversation> {
        return conversationRepository.getConversation(conversationId)
    }

    suspend fun fetchConversations(): Flow<List<Conversation>> {
        val conversationsFlow = conversationRepository.fetchConversations()
        scope.launch {
            streamConversations(null)
        }
        return conversationsFlow
    }

    suspend fun getLocalConversations() = conversationRepository.getLocalConversations()

    suspend fun forceRefreshConversations() = conversationRepository.forceRefreshConversations()

    suspend fun sendConversationMessage(
        title: String = "",
        conversationId: Long,
        userIds: List<String>,
        message: Message,
    ): Long {
        return conversationRepository.sendConversationMessage(
            title = title,
            conversationId = conversationId,
            userIds = userIds,
            message = message
        )
    }

    suspend fun createConversation(title: String, userIds: List<String>): Long {
        return conversationRepository.createConversation(title, userIds)
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

    private suspend fun streamConversations(
        token: String? = null,
    ) {
        conversationRepository.streamConversations(token)
    }

    suspend fun trackVideoProgress(
        token: String? = null,
        videoId: String,
        event: VideoProgressEvent,
        durationMs: Long,
    ) {
        Logger.d("TrackVideo: $videoId $event $durationMs")
        conversationRepository.trackVideoProgress(
            token = token,
            videoId = videoId,
            event = event,
            durationMs = durationMs
        )
    }

    suspend fun leaveConversation(conversationId: Long): Result<Boolean> {
        return conversationRepository.leaveConversation(conversationId)
    }

    suspend fun removeConversationParticipants(
        conversationId: Long,
        userIds: List<String>,
    ): Result<Boolean> {
        return conversationRepository.removeParticipants(conversationId, userIds)
    }

    suspend fun addConversationParticipants(
        conversationId: Long,
        userIds: List<String>,
    ): Result<Boolean> {
        return conversationRepository.addParticipants(conversationId, userIds)
    }

    suspend fun makeConversationAdmin(
        conversationId: Long,
        userId: String,
    ): Result<Boolean> {
        return conversationRepository.makeConversationAdmin(conversationId, userId)
    }

    suspend fun updateConversation(
        conversationId: Long,
        title: String,
    ): Result<Conversation> {
        return conversationRepository.updateConversation(conversationId, title)
    }

    suspend fun updateMessageStatus(conversationId: Long, messageId: Long) {
        conversationRepository.updateMessageStatus(conversationId, messageId)
    }

    suspend fun forceUpdateConversationMessage(conversation: Conversation) {
        conversation.messages.filterIndexed { _, message ->
            message.status == MessageStatus.SENDING && message.createdAt.minus(Clock.System.now()) > 5.minutes
        }.let { messages ->
            conversationRepository.upsertConversationMessages(
                conversationId = conversation.id,
                messages = messages.map {
                    it.copy(status = MessageStatus.FAILED)
                }
            )
        }
    }

    suspend fun getCurrentUser(
        forceRefresh: Boolean = false,
        onlyLocal: Boolean = false,
    ): Result<User> {
        return userRepository.getCurrentUser(forceRefresh, onlyLocal)
    }

    suspend fun pushTokens(token: String): Result<Boolean> {
        authRepository.savePushToken(token)
        return userRepository.pushTokens(token)
    }

    suspend fun verifyOTPCode(verificationId: String, otp: String): Result<String> {
        return userRepository.verifyOTPCode(verificationId, otp)
    }

    suspend fun updateUser(name: String, email: String? = null): Result<User> {
        return userRepository.updateUser(name, email)
    }

    suspend fun updateAvatar(name: String, type: String, files: ByteArray): Result<Boolean> {
        return userRepository.updateAvatar(name, type, files)
    }

    suspend fun logout(requireSuccess: Boolean = true): Result<Boolean> {
        return userRepository.logout(requireSuccess)
    }

    suspend fun getAuthToken(): String? = authRepository.getAuthToken().firstOrNull()

    suspend fun saveAuthToken(
        authToken: String,
    ) {
        timeSaveToken = Clock.System.now()
        authRepository.saveAuthToken(authToken)
    }

    suspend fun pushToken() {
        authRepository.getPushToken()?.let {
            userRepository.pushTokens(it)
        }
    }

    suspend fun delete() = authRepository.delete()

    suspend fun loadAllStickerPacks() = mediaRepository.loadAllStickerPacks()

    suspend fun downloadStickerPack(id: Long) = mediaRepository.downloadStickerPack(id)

    suspend fun getTrendingVideos(
        duration: TrendingVideoDuration,
    ) = videoRepository.getTrendingVideo(duration)

    suspend fun getVideos(isHighlight: Boolean) = videoRepository.getVideos(isHighlight)

    suspend fun getWatchingVideos() = videoRepository.getWatchingVideos()

    suspend fun getVideoDetail(id: String) = videoRepository.getVideoDetail(id)

    suspend fun commentVideo(
        id: String,
        comment: String,
        youtubeToken: String,
    ) = videoRepository.commentVideo(videoId = id, comment = comment, youtubeToken = youtubeToken)

    suspend fun getMemberInfo() = loyaltyRepository.getMemberInfo()

    suspend fun getLoyaltyPoints(
        filter: LoyaltyType? = null,
        month: Int? = null,
        year: Int? = null,
    ) = loyaltyRepository.getLoyaltyPoints(
        type = filter,
        month = month,
        year = year
    )

    fun onDestroy() {
        conversationRepository.onDestroy()
        scope.coroutineContext.cancel()
    }

    fun cancelTrackVideo() {
        socketManager.cancelTrackVideo()
    }
}