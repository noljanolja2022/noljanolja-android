package com.noljanolja.android.features.addfriend

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.CannotFindUsers
import com.noljanolja.android.common.error.PhoneNotAvailable
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddFriendViewModel : BaseViewModel() {
    private val _searchFriendUiStateFlow = MutableStateFlow<UiState<Nothing>>(UiState())
    val searchFriendUiStateFlow = _searchFriendUiStateFlow.asStateFlow()
    private val _searchUsersResultFlow = MutableStateFlow<List<User>>(emptyList())
    val searchUsersResultFlow = _searchUsersResultFlow.asStateFlow()
    private val _isProcessingInvite = MutableStateFlow(false)
    val isProcessingInvite = _isProcessingInvite.asStateFlow()
    fun handleEvent(event: AddFriendEvent) {
        launch {
            when (event) {
                AddFriendEvent.ShowResult -> navigationManager.navigate(NavigationDirections.SearchFriendResult)
                AddFriendEvent.Back -> back()
                AddFriendEvent.OpenCountries -> navigationManager.navigate(NavigationDirections.CountryPicker)
                is AddFriendEvent.SearchByPhone -> searchByPhone(event.phone)
                is AddFriendEvent.SearchById -> searchById(event.id)
                is AddFriendEvent.ShowError -> sendError(event.error)
                AddFriendEvent.ScanQrCode -> navigationManager.navigate(NavigationDirections.ScanQrCode)
                is AddFriendEvent.AddFriend -> addFriendById(event.id, event.name)
            }
        }
    }

    private suspend fun searchByPhone(phone: String) {
        _searchFriendUiStateFlow.emit(UiState(loading = true))
        val result = coreManager.findContacts(phoneNumber = phone)
        if (result.isSuccess && !result.getOrNull().isNullOrEmpty()) {
            _searchFriendUiStateFlow.emit(UiState())
            _searchUsersResultFlow.emit(result.getOrNull()!!)
            navigationManager.navigate(NavigationDirections.SearchFriendResult)
        } else {
            sendError(PhoneNotAvailable)
            _searchFriendUiStateFlow.emit(
                UiState(
                    error = result.exceptionOrNull() ?: PhoneNotAvailable
                )
            )
        }
    }

    private suspend fun searchById(id: String) {
        _searchFriendUiStateFlow.emit(UiState(loading = true))
        val result = coreManager.findContacts(friendId = id)
        if (result.isSuccess && !result.getOrNull().isNullOrEmpty()) {
            _searchFriendUiStateFlow.emit(UiState())
            _searchUsersResultFlow.emit(result.getOrNull()!!)
            navigationManager.navigate(NavigationDirections.SearchFriendResult)
        } else {
            _searchFriendUiStateFlow.emit(
                UiState(
                    error = result.exceptionOrNull() ?: CannotFindUsers
                )
            )
        }
    }

    private suspend fun addFriendById(id: String, name: String) {
        _isProcessingInvite.emit(true)
        val result = coreManager.inviteFriend(id)
        if (result.isSuccess) {
            navigationManager.navigate(
                NavigationDirections.Chat(
                    conversationId = 0,
                    userIds = id,
                    title = name,
                )
            )
        } else {
            result.exceptionOrNull()?.let {
                sendError(it)
            }
        }
        _isProcessingInvite.emit(false)
    }
}