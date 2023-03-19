package com.noljanolja.android.features.home.chat

import com.noljanolja.android.MyApplication
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : BaseViewModel() {
    private var conversationId: Long = 0L
    private var userId: String = ""
    private var userName: String = ""
    private var noMoreMessages: Boolean = false

    private var job: Job? = null
    private var seenJob: Job? = null

    private val _chatUiStateFlow = MutableStateFlow(UiState<Conversation>())
    val chatUiStateFlow = _chatUiStateFlow.asStateFlow()

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
            }
        }
    }

    fun setupConversation(conversationId: Long, userId: String, userName: String) {
        launch {
            this.conversationId = conversationId
            this.userId = userId
            this.userName = userName
            _chatUiStateFlow.emit(
                UiState(
                    data = createEmptyConversation()
                )
            )
            fetchConversation()
        }
    }

    private suspend fun sendMessage(message: Message) {
        coreManager.sendConversationMessage(conversationId, userId, message)
            .takeIf { it > 0L && conversationId == 0L }?.let {
            conversationId = it
            fetchConversation(onlyLocalData = true)
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
                coreManager.findConversationWithUser(userId)?.let {
                    conversationId = it.id
                    updateUiState(it)
                }
            }
            if (conversationId != 0L) {
                MyApplication.latestConversationId = conversationId
                coreManager.getConversation(conversationId).collect { conversation ->
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
            title = "",
            type = ConversationType.SINGLE,
            creator = User(),
            participants = listOf(User(name = userName)),
        )
    }

    private suspend fun updateUiState(conversation: Conversation) {
        _chatUiStateFlow.emit(
            UiState(
                data = conversation
            )
        )
        seenJob?.cancel()
        seenJob = launch {
            conversation.messages.firstOrNull()?.let { message ->
                if (!message.isSeenByMe) {
                    coreManager.updateMessageStatus(conversationId, message.id)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        MyApplication.latestConversationId = 0L
    }
}