package com.noljanolja.android.features.addfriend

sealed interface AddFriendEvent {
    object ShowResult : AddFriendEvent
    object Back : AddFriendEvent
    object OpenCountries : AddFriendEvent
    data class SearchByPhone(val phone: String) : AddFriendEvent
    data class SearchById(val id: String) : AddFriendEvent
    object ScanQrCode : AddFriendEvent
    data class ShowError(val error: Throwable) : AddFriendEvent
    data class AddFriend(val id: String, val name: String) : AddFriendEvent
}