package com.noljanolja.android.features.conversationmedia

sealed interface ConversationMediaEvent {
    object Back : ConversationMediaEvent
    data class ViewImages(val image: String) : ConversationMediaEvent
    object LoadMoreImage : ConversationMediaEvent
    object LoadMoreFile : ConversationMediaEvent
    object LoadMoreLink : ConversationMediaEvent
}