package com.noljanolja.android.features.home.conversations

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.conversation.domain.model.Conversation
import com.noljanolja.android.common.conversation.domain.model.ConversationType
import com.noljanolja.android.common.conversation.domain.model.Message
import com.noljanolja.android.common.conversation.domain.model.MessageType
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<List<Conversation>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        fetchConversations()
    }

    fun handleEvent(event: ConversationsEvent) {
        launch {
            when (event) {
                is ConversationsEvent.OpenConversation -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = event.conversationId,
                            userId = event.userId,
                            userName = event.userName
                        )
                    )
                }
                is ConversationsEvent.OpenContactPicker -> {
                    navigationManager.navigate(NavigationDirections.SelectContact)
                }
            }
        }
    }

    private fun fetchConversations() {
        launch {
            _uiStateFlow.emit(
                UiState(
                    data = listOf(
                        Conversation(
                            id = 0,
                            title = "Test conversations",
                            type = ConversationType.Single,
                            creator = User(
                                name = "Jenny"
                            ),
                            messages = listOf(
                                Message(
                                    message = "Hihihaha",
                                    type = MessageType.PlainText
                                )
                            ),
                            participants = listOf(
                                User(
                                    name = "Jenny"
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}