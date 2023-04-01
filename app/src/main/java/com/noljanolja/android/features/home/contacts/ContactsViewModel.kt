package com.noljanolja.android.features.home.contacts

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import org.koin.core.component.inject

class ContactsViewModel(
    private val type: ConversationType,
) : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val permissionChecker: PermissionChecker by inject()
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _selectedUserFlow = MutableStateFlow<List<User>>(
        emptyList()
    )
    val selectedUserFlow = _selectedUserFlow.asStateFlow()

    init {
        if (permissionChecker.canReadContacts()) handleEvent(ContactsEvent.SyncContacts)
    }

    fun handleEvent(event: ContactsEvent) {
        launch {
            when (event) {
                ContactsEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                ContactsEvent.OpenPhoneSettings -> {
                    navigationManager.navigate(NavigationDirections.PhoneSettings)
                }
                ContactsEvent.SyncContacts -> getFriends()
                is ContactsEvent.Chat -> {
                    val contacts = selectedUserFlow.value
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = 0,
                            userIds = contacts.joinToString(",") { it.id },
                            title = contacts.joinToString(", ") { it.name }
                        )
                    )
                }
                is ContactsEvent.SelectContact -> {
                    when (type) {
                        ConversationType.GROUP -> {
                            val contact = event.contact
                            val value = _selectedUserFlow.value
                            if (value.contains(contact)) {
                                _selectedUserFlow.emit(value.filter { it != contact })
                            } else {
                                _selectedUserFlow.emit(value + listOf(contact))
                            }
                        }
                        ConversationType.SINGLE -> {
                            navigationManager.navigate(
                                NavigationDirections.Chat(
                                    conversationId = 0,
                                    userIds = event.contact.id,
                                    title = event.contact.name
                                )
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private suspend fun getFriends() {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(value.copy(loading = true))
        val result = coreManager.getFriends()
        if (result.isSuccess) {
            _uiStateFlow.emit(UiState(data = result.getOrDefault(listOf())))
        } else {
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }

    private suspend fun syncContacts() {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(value.copy(loading = true))
        val loadedContacts = contactsLoader.loadContacts().toList()
        val result = coreManager.syncUserContacts(loadedContacts)
        if (result.isSuccess) {
            _uiStateFlow.emit(UiState(data = result.getOrDefault(listOf())))
        } else {
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }
}