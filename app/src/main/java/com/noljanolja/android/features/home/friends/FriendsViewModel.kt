package com.noljanolja.android.features.home.friends

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import org.koin.core.component.inject

class FriendsViewModel : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val permissionChecker: PermissionChecker by inject()
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private var page: Int = 1
    private var noMoreContact: Boolean = false

    init {
        if (permissionChecker.canReadContacts()) handleEvent(FriendsEvent.GetContacts)
    }

    fun handleEvent(event: FriendsEvent) {
        launch {
            when (event) {
                FriendsEvent.OpenPhoneSettings -> {
                    navigationManager.navigate(NavigationDirections.PhoneSettings)
                }

                is FriendsEvent.OpenFriendOption -> {
                    event.run {
                        navigationManager.navigate(
                            NavigationDirections.FriendOption(
                                friendId = friendId,
                                friendName = friendName,
                                friendAvatar = friendAvatar
                            )
                        )
                    }
                }

                FriendsEvent.GetContacts -> getContacts()
                FriendsEvent.SyncContacts -> syncContacts()

                is FriendsEvent.LoadMore -> {
                    if (noMoreContact) return@launch
                    getContacts()
                }

                FriendsEvent.AddFriend -> {
                    navigationManager.navigate(NavigationDirections.AddFriend)
                }

                FriendsEvent.InviteFriend -> {
                    navigationManager.navigate(NavigationDirections.Referral)
                }

                FriendsEvent.Setting -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }
            }
        }
    }

    private suspend fun getContacts() {
        val value = _uiStateFlow.value
        if (page == 1) {
            _uiStateFlow.emit(value.copy(loading = true))
        }
        val result = coreManager.getContacts(page)
        if (result.isSuccess) {
            page++
            val contacts = result.getOrDefault(emptyList()).also {
                noMoreContact = it.isEmpty()
            }
            val newData = (value.data.orEmpty() + contacts).distinctBy { it.id }
            _uiStateFlow.emit(UiState(data = newData))
        } else {
            noMoreContact = true
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }

    private suspend fun syncContacts() {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(value.copy(loading = true))
        val loadedContacts = contactsLoader.loadContacts().toList()
        val result = coreManager.syncUserContacts(loadedContacts)
        if (result.isSuccess) {
            page = 1
            getContacts()
        } else {
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }

    private suspend fun findContacts(phoneNumber: String) {
        val value = _uiStateFlow.value
        val result = coreManager.findContacts(phoneNumber)
        if (result.isSuccess) {
            _uiStateFlow.emit(UiState(data = result.getOrDefault(emptyList())))
        } else {
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }
}