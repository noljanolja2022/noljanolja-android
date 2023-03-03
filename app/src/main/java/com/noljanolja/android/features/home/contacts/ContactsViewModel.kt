package com.noljanolja.android.features.home.contacts

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.common.contact.domain.repository.ContactsRepository
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val contactsRepository: ContactsRepository,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<List<Contact>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            syncContacts()
        }
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
                is ContactsEvent.SearchFriend -> TODO()
                ContactsEvent.SyncContacts -> syncContacts()
            }
        }
    }

    private suspend fun syncContacts() {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(value.copy(loading = true))
        contactsRepository.syncContacts().collect {
            if (it.isSuccess) {
                _uiStateFlow.emit(UiState(data = it.getOrDefault(listOf())))
            } else {
                _uiStateFlow.emit(value.copy(error = it.exceptionOrNull()))
            }
        }
    }
}