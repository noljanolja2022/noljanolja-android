package com.noljanolja.android.features.home.friends

import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.mobiledata.data.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.services.*
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

class FriendsViewModel : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val permissionChecker: PermissionChecker by inject()
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val _needReadStateFlow = MutableStateFlow(false)
    val needReadStateFlow = _needReadStateFlow.asStateFlow()

    private var page: Int = 1
    private var noMoreContact: Boolean = false

    init {
        if (permissionChecker.canReadContacts()) handleEvent(FriendsEvent.GetContacts)
        getNotifications()
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

                FriendsEvent.OpenSettingScreen -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }

                FriendsEvent.OpenNotificationScreen -> {
                    navigationManager.navigate(NavigationDirections.FriendNotifications)
                }

                FriendsEvent.OpenSearchScreen -> {
                    navigationManager.navigate(NavigationDirections.SearchFriendByName)
                }
            }
        }
    }

    private fun getNotifications() {
        launch {
            _isLoading.emit(true)
            val result = coreManager.getNotifications(
                GetNotificationsRequest()
            )
            _isLoading.emit(false)

            if (result.isSuccess) {
                _needReadStateFlow.emit(
                    result.getOrDefault(emptyList()).any { !it.isRead }
                )
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