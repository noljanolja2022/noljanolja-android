package com.noljanolja.android.features.home.chat_options

import co.touchlab.kermit.Logger
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.utils.isAdminOfConversation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatOptionsViewModel(private val conversationId: Long) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<Conversation>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val _updateConversationSuccessEvent = MutableSharedFlow<Unit>()
    val updateConversationSuccessEvent = _updateConversationSuccessEvent.asSharedFlow()
    private val _isAdminOfConversation = MutableStateFlow<Boolean>(false)
    val isAdminOfConversation = _isAdminOfConversation.asStateFlow()

    init {
        launch {
            coreManager.getConversation(conversationId).collect {
                _uiStateFlow.emit(UiState(data = it))
                coreManager.getCurrentUser().getOrNull()?.let { user ->
                    _isAdminOfConversation.emit(
                        user.isAdminOfConversation(it)
                    )
                }
            }
        }
    }

    fun handleEvent(event: ChatOptionsEvent) {
        launch {
            when (event) {
                ChatOptionsEvent.Back -> back()
                ChatOptionsEvent.AddContact -> {
                    navigationManager.navigate(
                        NavigationDirections.SelectContact(
                            type = ConversationType.GROUP.name,
                            conversationId = conversationId,
                        )
                    )
                }

                ChatOptionsEvent.EditTitle -> {
                    navigationManager.navigate(
                        NavigationDirections.EditChatTitle(
                            conversationId = conversationId
                        )
                    )
                }

                ChatOptionsEvent.LeaveConversation -> {
                    coreManager.leaveConversation(conversationId).let {
                        if (it.isSuccess) {
                            navigationManager.navigate(
                                NavigationDirections.Home
                            )
                        } else {
                            sendError(it.exceptionOrNull()!!)
                        }
                    }
                }

                is ChatOptionsEvent.RemoveParticipant -> {
                    removeParticipants(listOf(event.id))
                }

                is ChatOptionsEvent.MakeAdminConversation -> {
                    makeConversationAdmin(event.id)
                }

                is ChatOptionsEvent.BlockUser -> {
                    Logger.e("Block user ${event.id}")
                }

                ChatOptionsEvent.ShowMedias -> navigationManager.navigate(
                    NavigationDirections.ConversationMedia(
                        conversationId
                    )
                )
            }
        }
    }

    private suspend fun removeParticipants(userIds: List<String>) {
        coreManager.removeConversationParticipants(conversationId, userIds).exceptionOrNull()?.let {
            sendError(it)
        } ?: _updateConversationSuccessEvent.emit(Unit)
    }

    private suspend fun makeConversationAdmin(userId: String) {
        coreManager.makeConversationAdmin(conversationId, userId).exceptionOrNull()?.let {
            sendError(it)
        } ?: _updateConversationSuccessEvent.emit(Unit)
    }
}