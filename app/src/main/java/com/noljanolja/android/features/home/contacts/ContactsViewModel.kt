package com.noljanolja.android.features.home.contacts

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.contact.domain.repository.ContactsRepository
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val contactsRepository: ContactsRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
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
                ContactsEvent.SyncContacts -> syncContacts()
            }
        }
    }

    private suspend fun syncContacts() {
        val value = _uiStateFlow.value
        _uiStateFlow.emit(value.copy(loading = true))
        contactsRepository.syncContacts().getOrDefault(listOf()).let { loadedContacts ->
            val result = userRepository.syncUserContacts(loadedContacts)
            if (result.isSuccess) {
                _uiStateFlow.emit(UiState(data = result.getOrDefault(listOf())))
            } else {
                _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
            }
        }
    }
}