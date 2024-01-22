package com.noljanolja.android.features.home.searchchat

/**
 * Created by tuyen.dang on 1/16/2024.
 */

sealed interface SearchChatEvent {
    object Back : SearchChatEvent
    data class Search(val text: String) : SearchChatEvent
    object ClearAll : SearchChatEvent
    data class Clear(val text: String) : SearchChatEvent

    data class OpenConversation(
        val conversationId: Long = 0,
    ) : SearchChatEvent
}
