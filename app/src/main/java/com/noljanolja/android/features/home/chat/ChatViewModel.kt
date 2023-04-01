package com.noljanolja.android.features.home.chat

import android.net.Uri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.koin.core.component.inject

class ChatViewModel : BaseViewModel() {
    private val mediaLoader: MediaLoader by inject()

    private var conversationId: Long = 0L
    private var userIds: List<String> = emptyList()
    private var title: String = ""
    private var noMoreMessages: Boolean = false
    private var forceUpdateConversation: Boolean = true
    private var lastMessageId: String = ""
    private var job: Job? = null
    private var seenJob: Job? = null

    private val _chatUiStateFlow = MutableStateFlow(UiState<Conversation>())
    val chatUiStateFlow = _chatUiStateFlow.asStateFlow()

    private val _loadedMediaFlow = MutableStateFlow<List<Pair<Uri, Long?>>>(emptyList())
    val loadedMediaFlow = _loadedMediaFlow.asStateFlow()

    private val _scrollToNewMessageEvent = MutableSharedFlow<Unit>()
    val scrollToNewMessageEvent = _scrollToNewMessageEvent.asSharedFlow()

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
                    sendMessage(event.message)
                }
                ChatEvent.LoadMedia -> loadMedia()
                ChatEvent.OpenPhoneSettings -> {}
            }
        }
    }

    fun setupConversation(
        conversationId: Long,
        userIds: List<String>,
        title: String,
    ) {
        launch {
            this.conversationId = conversationId
            this.userIds = userIds
            this.title = title
            _chatUiStateFlow.emit(
                UiState(
                    data = createEmptyConversation()
                )
            )
            fetchConversation()
        }
    }

    private suspend fun sendMessage(message: Message) {
        // call with main scope to avoid cancel when back
        launchInMain {
            withContext(Dispatchers.IO) {
                coreManager.sendConversationMessage(
                    title = title,
                    conversationId = conversationId,
                    userIds = userIds,
                    message = message
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
            if (conversationId != 0L) {
                MyApplication.latestConversationId = conversationId
                coreManager.getConversation(conversationId).collect { conversation ->
                    forceUpdateConversation(conversation)
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
                conversation.messages.firstOrNull()?.let { message ->
                    if (!message.isSeenByMe) {
                        coreManager.updateMessageStatus(conversationId, message.id)
                    }
                }
            }
        }
        conversation.messages.firstOrNull()?.let {
            if (it.localId != lastMessageId) {
                lastMessageId = it.localId
                delay(10L)
                _scrollToNewMessageEvent.emit(Unit)
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

    private suspend fun forceUpdateConversation(conversation: Conversation) {
        if (!forceUpdateConversation) return
        coreManager.forceUpdateConversation(conversation)
        forceUpdateConversation = false
    }

    override fun onCleared() {
        super.onCleared()
        MyApplication.latestConversationId = 0L
    }
}