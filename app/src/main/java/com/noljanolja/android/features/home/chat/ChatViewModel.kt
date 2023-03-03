package com.noljanolja.android.features.home.chat

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.conversation.domain.model.Conversation
import com.noljanolja.android.common.conversation.domain.model.ConversationType
import com.noljanolja.android.common.conversation.domain.model.Message
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : BaseViewModel() {
    private var conversationId: Long = 0L
    private var userId: String = ""
    private var userName: String = ""
    private var job: Job? = null

    private val _chatUiStateFlow = MutableStateFlow(UiState<Conversation>())
    val chatUiStateFlow = _chatUiStateFlow.asStateFlow()

    fun handleEvent(event: ChatEvent) {
        launch {
            when (event) {
                is ChatEvent.ClickMessage -> {}
                ChatEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
                ChatEvent.LoadMoreMessages -> {}
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
        val conversation = _chatUiStateFlow.value.data ?: return
        val messages = conversation.messages
        val newMessages = mutableListOf<Message>().also {
            it.add(
                message.copy(
                    sender = User(isMe = true)
                )
            )
            it.addAll(messages)
        }
        _chatUiStateFlow.emit(UiState(data = conversation.copy(messages = newMessages)))
        testReply(message)
    }

    // Test
    private suspend fun testReply(message: Message) {
        delay(500)
        Random.nextInt(2).takeIf { it > 0 } ?: return
        val conversation = _chatUiStateFlow.value.data ?: return
        val messages = conversation.messages
        val newMessages = mutableListOf<Message>().also {
            it.add(
                message.copy(
                    message = "Reply ${message.message}"
                )
            )
            it.addAll(messages)
        }
        _chatUiStateFlow.emit(UiState(data = conversation.copy(messages = newMessages)))
    }

    private fun fetchConversation() {
        job?.cancel()
        job = launch {
            val value = _chatUiStateFlow.value
            _chatUiStateFlow.emit(value.copy(loading = true))
            delay(1_000)
            _chatUiStateFlow.emit(
                UiState(
                    data = Conversation.mock().copy(id = conversationId)
                )
            )
        }
    }

    private fun createEmptyConversation(): Conversation {
        return Conversation(
            id = 0,
            title = "",
            type = ConversationType.Single,
            creator = User(),
            participants = listOf(User(name = userName)),
        )
    }
}