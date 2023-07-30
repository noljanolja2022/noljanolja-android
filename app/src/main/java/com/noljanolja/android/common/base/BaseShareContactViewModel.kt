package com.noljanolja.android.common.base

import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.android.features.common.toShareContact
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

open class BaseShareContactViewModel : BaseViewModel() {
    private val _contactsFlow = MutableStateFlow<List<ShareContact>>(emptyList())
    val contactsFlow = _contactsFlow.asStateFlow()

    protected val _shareSuccessEvent = MutableSharedFlow<Unit>()
    val shareSuccessEvent = _shareSuccessEvent.asSharedFlow()

    private var page: Int = 1
    private var noMoreContact: Boolean = false

    init {
        launch {
            getShareContacts()
        }
    }

    protected suspend fun getShareContacts() {
        if (page == 1) {
            val conversationContacts = coreManager.getLocalConversations().first().map {
                it.toShareContact()
            }
            getUserContacts(conversationContacts)
        } else {
            val gotContacts = _contactsFlow.value
            getUserContacts(gotContacts)
        }
    }

    private suspend fun getUserContacts(
        gotContacts: List<ShareContact>,
    ) {
        val result = coreManager.getContacts(page)
        val contacts: List<ShareContact>
        if (result.isSuccess) {
            page++
            contacts = result.getOrDefault(emptyList()).also {
                noMoreContact = it.isEmpty()
            }.filter { !checkHasContact(gotContacts, it.id) }.map {
                ShareContact(
                    userId = it.id,
                    title = it.name,
                    avatar = it.avatar
                )
            }
        } else {
            contacts = emptyList()
            noMoreContact = true
        }
        _contactsFlow.emit(gotContacts + contacts)
    }

    private fun checkHasContact(contacts: List<ShareContact>, userId: String): Boolean {
        return contacts.any { it.userId == userId }
    }
}