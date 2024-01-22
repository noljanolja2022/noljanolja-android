package com.noljanolja.android.features.home.searchchat

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.extensions.*
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 1/16/2024.
 */

class SearchChatViewModel : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<List<Conversation>>(emptyList())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val searchKeys = coreManager.getConversationSearchHistories().map { data ->
        data.sortedByDescending { it.updatedAt }.map { it.text }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun handleEvent(event: SearchChatEvent) {
        launch {
            when (event) {
                SearchChatEvent.Back -> back()
                is SearchChatEvent.Clear -> {
                    coreManager.clearConversationTextSearch(event.text)
                }

                SearchChatEvent.ClearAll -> {
                    coreManager.clearAllConversationSearch()
                }

                is SearchChatEvent.OpenConversation -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = event.conversationId,
                        )
                    )
                }

                is SearchChatEvent.Search -> searchChat(event.text)
            }
        }
    }

    private suspend fun searchChat(query: String) {
        _isLoading.emit(true)
        coreManager.insertConversationSearchKey(query)
        coreManager.fetchConversations().collect { data ->
            _uiStateFlow.run {
                val queryConversation = Conversation(
                    id = query.convertToLong(),
                    title = query,
                    creator = User(
                        id = query,
                        name = query,
                        email = query,
                        phone = query
                    ),
                    admin = User(
                        id = query,
                        name = query,
                        email = query,
                        phone = query
                    )
                )
                _isLoading.emit(false)
                emit(
                    data.filter {
                        it.compareConversation(queryConversation)
                    }
                )
            }
        }
    }
}
