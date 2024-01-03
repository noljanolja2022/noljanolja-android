package com.noljanolja.android.features.home.friendoption

/**
 * Created by tuyen.dang on 11/14/2023.
 */

sealed interface FriendOptionEvent {
    object GoBack : FriendOptionEvent

    object GoToChatScreen : FriendOptionEvent

    data class GoToSendPointScreen(
        val friendAvatar: String,
        val isRequestPoint: Boolean
    ) : FriendOptionEvent
}