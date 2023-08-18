package com.noljanolja.android.features.home.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.noljanolja.android.MyApplication
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.mobiledata.data.MediaLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.isNormalType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import java.util.UUID.randomUUID

class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    private var conversationId: Long = 0L,
    private var userIds: List<String> = emptyList(),
    private var title: String = "",
) : BaseViewModel() {
    private val mediaLoader: MediaLoader by inject()
    private var noMoreMessages: Boolean = false
    private var forceUpdateConversation: Boolean = true
    private var lastMessageId: String = ""
    private var job: Job? = null
    private var seenJob: Job? = null
    private var selectShareMessage: Message? = null

    private val _chatUiStateFlow = MutableStateFlow(UiState<Conversation>())
    val chatUiStateFlow = _chatUiStateFlow.asStateFlow()

    private val _loadedMediaFlow = MutableStateFlow<List<Pair<Uri, Long?>>>(emptyList())
    val loadedMediaFlow = _loadedMediaFlow.asStateFlow()

    val reactIconsFlow = coreManager.getReactIcons().stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000)
    )

    init {
        launch {
            fetchConversation()
        }
        launch {
            coreManager.getRemovedConversationEvent().collect {
                if (it.id == conversationId) {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            }
        }
    }

    fun handleEvent(event: ChatEvent) {
        launch {
            when (event) {
                is ChatEvent.ClickMessage -> {}
                ChatEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
                ChatEvent.LoadMoreMessages -> {
                    if (noMoreMessages) return@launch

                    _chatUiStateFlow.value.data?.let { conversation ->
                        val oldestMessageId = conversation.messages.lastOrNull()?.id

                        coreManager.getConversationMessages(
                            conversationId = conversationId,
                            messageBefore = oldestMessageId,
                            messageAfter = null,
                        ).takeIf { it.isNotEmpty() }
                            ?: let { noMoreMessages = true }
                    }
                }

                is ChatEvent.NavigateToProfile -> {}
                is ChatEvent.ReloadConversation -> {}
                is ChatEvent.SendMessage -> {
                    sendMessage(event.message, event.replyToMessageId)
                }

                ChatEvent.LoadMedia -> loadMedia()
                ChatEvent.OpenPhoneSettings -> {}
                ChatEvent.ChatOptions -> {
                    navigationManager.navigate(NavigationDirections.ChatOptions(conversationId))
                }

                is ChatEvent.React -> {
                    coreManager.reactMessage(conversationId, event.messageId, event.reactId)
                }

                is ChatEvent.DeleteMessage -> deleteMessage(
                    event.messageId,
                    event.removeForSelfOnly
                )

                is ChatEvent.SelectShareMessage -> selectShareMessage(event.message)
                is ChatEvent.ShareMessage -> shareMessage(event.conversationIds, event.userIds)
                is ChatEvent.OpenVideo -> {
                    navigationManager.navigate(NavigationDirections.PlayScreen(videoId = event.id))
                }

                is ChatEvent.ViewImages -> {
                    navigationManager.navigate(NavigationDirections.ViewImages(images = event.images))
                }
            }
        }
    }

    private suspend fun sendMessage(
        message: Message,
        replyToMessageId: Long?,
    ) {
        // call with main scope to avoid cancel when back
        launchInMain {
            withContext(Dispatchers.IO) {
                coreManager.sendConversationMessage(
                    title = title,
                    conversationId = conversationId,
                    userIds = userIds,
                    message = message,
                    replyToMessageId = replyToMessageId,
                )
                    .takeIf { it > 0L && conversationId == 0L }?.let {
                    conversationId = it
                    fetchConversation(onlyLocalData = true)
                }
            }
        }
    }

    private fun fetchConversation(onlyLocalData: Boolean = false) {
        job?.cancel()
        job = launch {
            val value = _chatUiStateFlow.value
            if (!onlyLocalData) {
                _chatUiStateFlow.emit(value.copy(loading = true))
            }
            if (conversationId == 0L) {
                coreManager.findConversationWithUsers(userIds)?.let {
                    conversationId = it.id
                    updateUiState(it)
                }
            }
            if (conversationId == 0L && userIds.size > 1) {
                coreManager.createConversation(title, userIds).let {
                    conversationId = it
                }
            }
            if (conversationId != 0L) {
                MyApplication.latestConversationId = conversationId
                coreManager.getConversation(conversationId).collect { conversation ->
                    forceUpdateConversationMessage(conversation)
                    updateUiState(conversation)
                }
            } else {
                updateUiState(createEmptyConversation())
            }
        }
    }

    private fun createEmptyConversation(): Conversation {
        return Conversation(
            id = 0,
            title = title,
            type = if (userIds.size > 1) ConversationType.GROUP else ConversationType.SINGLE,
            creator = User(),
            admin = User(),
            participants = title.split(", ").map { User(name = it) },
        )
    }

    private suspend fun updateUiState(conversation: Conversation) {
        _chatUiStateFlow.emit(
            UiState(
                data = conversation
            )
        )
        seenJob?.cancel()
        seenJob = launchInMain {
            withContext(Dispatchers.IO) {
                conversation.messages.firstOrNull { !it.sender.isMe && it.isNormalType() }
                    ?.let { message ->
                        if (!message.isSeenByMe) {
                            coreManager.updateMessageStatus(conversationId, message.id)
                        }
                    }
            }
        }
    }

    private fun loadMedia() {
        launch {
            val localImages =
                mediaLoader.loadMedia().toList()
            _loadedMediaFlow.emit(localImages)
        }
    }

    private suspend fun deleteMessage(messageId: Long, removeForSelfOnly: Boolean) {
        coreManager.deleteMessage(
            conversationId = conversationId,
            messageId = messageId,
            removeForSelfOnly = removeForSelfOnly
        ).exceptionOrNull()?.let {
            sendError(it)
        }
    }

    private suspend fun selectShareMessage(message: Message) {
        selectShareMessage = message
        navigationManager.navigate(
            NavigationDirections.SelectShareMessage(
                selectMessageId = message.id,
                fromConversationId = conversationId
            )
        )
    }

    private fun shareMessage(conversationsIds: List<Long>?, userIds: List<String>?) {
        val message = selectShareMessage?.copy(
            id = 0L,
            _localId = randomUUID().toString()
        ) ?: return
        launchInMain {
            withContext(Dispatchers.IO) {
                conversationsIds?.forEach { conversationId ->
                    // call with main scope to avoid cancel when back
                    coreManager.sendConversationMessage(
                        conversationId = conversationId,
                        userIds = userIds.orEmpty(),
                        message = message,
                        replyToMessageId = null,
                        shareMessageId = selectShareMessage!!.id,
                    )
                }
            }
        }
    }

    private suspend fun forceUpdateConversationMessage(conversation: Conversation) {
        if (!forceUpdateConversation) return
        coreManager.forceUpdateConversationMessage(conversation)
        forceUpdateConversation = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        MyApplication.latestConversationId = 0L
    }
}