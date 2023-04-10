package com.noljanolja.android.features.home.contacts

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject

@OptIn(FlowPreview::class)
class ContactsViewModel(
    private val type: ConversationType,
    private val conversationId: Long,
) : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val permissionChecker: PermissionChecker by inject()
    private val _uiStateFlow = MutableStateFlow(UiState<List<User>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _selectedUserFlow = MutableStateFlow<List<User>>(
        emptyList()
    )
    val selectedUserFlow = _selectedUserFlow.asStateFlow()
    private val searchTextFlow = MutableStateFlow<String>("")

    private var page: Int = 1
    private var noMoreContact: Boolean = false

    init {
        if (permissionChecker.canReadContacts()) handleEvent(ContactsEvent.GetContacts)
        launch {
//            searchTextFlow.debounce(300).collectLatest {
//                findContacts(it)
//            }
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
                ContactsEvent.GetContacts -> getContacts()
                ContactsEvent.SyncContacts -> syncContacts()
                is ContactsEvent.ConfirmContacts -> {
                    val contacts = selectedUserFlow.value
                    if (conversationId > 0) {
                        addParticipants(contacts.map { it.id })
                    } else {
                        navigationManager.navigate(
                            NavigationDirections.Chat(
                                conversationId = 0,
                                userIds = contacts.joinToString(",") { it.id },
                                title = contacts.firstOrNull()?.name.takeIf { contacts.size == 1 }
                                    .orEmpty()
                            )
                        )
                    }
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
                                    title = event.contact.name,
                                )
                            )
                        }
                        else -> Unit
                    }
                }
                is ContactsEvent.LoadMore -> {
                    if (noMoreContact) return@launch
                    getContacts()
                }
                is ContactsEvent.SearchContact -> {
                    searchTextFlow.emit(event.searchText)
                }
            }
        }
    }

    private suspend fun addParticipants(userIds: List<String>) {
        coreManager.addConversationParticipants(
            conversationId,
            userIds,
        ).also {
            if (it.isSuccess) {
                navigationManager.navigate(NavigationDirections.Back)
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