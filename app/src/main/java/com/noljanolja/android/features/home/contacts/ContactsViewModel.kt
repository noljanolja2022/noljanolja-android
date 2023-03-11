package com.noljanolja.android.features.home.contacts

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.contact.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.core.CoreManager
import com.noljanolja.core.user.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val coreManager: CoreManager,
    private val contactsLoader: ContactsLoader,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    fun handleEvent(event: ContactsEvent) {
        launch {
            when (event) {
                ContactsEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                ContactsEvent.OpenPhoneSettings -> {
                    navigationManager.navigate(NavigationDirections.PhoneSettings)
                }
                ContactsEvent.SyncContacts -> syncContacts()
                is ContactsEvent.Chat -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = 0,
                            userId = event.contact.id,
                            userName = event.contact.name
                        )
                    )
                }
            }
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