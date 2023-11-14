package com.noljanolja.android.features.home.friends

sealed interface FriendsEvent {
    object GetContacts : FriendsEvent

    object SyncContacts : FriendsEvent

    object LoadMore : FriendsEvent

    object OpenPhoneSettings : FriendsEvent

    object AddFriend : FriendsEvent

    data class OpenFriendOption(
        val friendId: String,
        val friendName: String,
        val friendAvatar: String
    ) : FriendsEvent
}