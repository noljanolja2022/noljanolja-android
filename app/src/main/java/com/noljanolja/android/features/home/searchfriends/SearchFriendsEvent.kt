package com.noljanolja.android.features.home.searchfriends

/**
 * Created by tuyen.dang on 1/16/2024.
 */

sealed interface SearchFriendsEvent {
    object Back : SearchFriendsEvent
    data class Search(val text: String) : SearchFriendsEvent
    object ClearAll : SearchFriendsEvent
    data class Clear(val text: String) : SearchFriendsEvent

    data class OpenFriendOption(
        val friendId: String,
        val friendName: String,
        val friendAvatar: String
    ) : SearchFriendsEvent
}
