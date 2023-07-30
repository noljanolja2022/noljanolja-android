package com.noljanolja.android.features.sharemessage

import android.content.Context
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.android.features.common.toShareContact
import com.noljanolja.android.util.getUriFromCache
import com.noljanolja.android.util.loadFileInfo
import com.noljanolja.core.conversation.domain.model.MessageAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID.randomUUID

class SelectShareMessageViewModel(
    private val selectMessageId: Long,
) : BaseViewModel() {
    private var context: Context? = null

    private val _uiStateFlow = MutableStateFlow<UiState<List<ShareContact>>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _selectedContactFlow = MutableStateFlow<List<ShareContact>>(
        emptyList()
    )
    val selectedContactFlow = _selectedContactFlow.asStateFlow()

    private val _sharedEvent = MutableSharedFlow<Unit>()
    val sharedEvent = _sharedEvent.asSharedFlow()

    private var page: Int = 1
    private var noMoreContact: Boolean = false

    init {
        launch {
            val contacts = mutableListOf<ShareContact>()
            val localConversations = coreManager.getLocalConversations().first()
            contacts.addAll(
                localConversations.map { conversation ->
                    conversation.toShareContact()
                }
            )
            _uiStateFlow.emit(UiState(data = contacts))
            getContacts()
        }
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun handleEvent(event: SelectShareMessageEvent) {
        launch {
            when (event) {
                is SelectShareMessageEvent.Select -> {
                    val contact = event.contact
                    val value = _selectedContactFlow.value
                    if (value.contains(contact)) {
                        _selectedContactFlow.emit(value.filter { it != contact })
                    } else {
                        _selectedContactFlow.emit(value + listOf(contact))
                    }
                }

                is SelectShareMessageEvent.Back -> back()
                SelectShareMessageEvent.Share -> shareMessage()
            }
        }
    }

    private fun shareMessage() {
        launchInMain {
            withContext(Dispatchers.IO) {
                val message = coreManager.getMessageById(selectMessageId) ?: return@withContext
                val uiData = _uiStateFlow.value.data
                _uiStateFlow.emit(UiState(loading = true, data = uiData))
                with(_selectedContactFlow.value) {
                    val conversationIds = this.mapNotNull { it.conversationId }
                    val userIds =
                        this.mapNotNull { contact -> contact.userId.takeIf { contact.conversationId == null } }
                    val sendMessage = message.copy(
                        _localId = randomUUID().toString(),
                        id = 0L,
                        attachments = message.attachments.mapNotNull { attachment ->
                            val uri =
                                context?.getUriFromCache("${message.localId}/${attachment.originalName}")
                            val fileInfo = uri?.let { context?.loadFileInfo(it) }
                            fileInfo?.let {
                                MessageAttachment(
                                    name = "",
                                    originalName = fileInfo.name,
                                    type = fileInfo.contentType,
                                    size = fileInfo.contents.size.toLong(),
                                    contents = fileInfo.contents,
                                    localPath = fileInfo.path.toString(),
                                )
                            }
                        }
                    )

                    (
                        conversationIds.map { conversationId ->
                            // call with main scope to avoid cancel when back
                            async {
                                coreManager.sendConversationMessage(
                                    conversationId = conversationId,
                                    userIds = emptyList(),
                                    message = sendMessage,
                                    replyToMessageId = null,
                                    shareMessageId = selectMessageId,
                                )
                            }
                        } + userIds.map { userId ->
                            async {
                                coreManager.sendConversationMessage(
                                    conversationId = 0L,
                                    userIds = listOf(userId),
                                    message = sendMessage,
                                    replyToMessageId = null,
                                    shareMessageId = selectMessageId,
                                )
                            }
                        }
                        ).awaitAll()
                    coreManager.forceRefreshConversations()
                    _uiStateFlow.emit(UiState(loading = false, data = uiData))
                    _sharedEvent.emit(Unit)
                    back()
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
            }.filter { !checkHasContact(value.data.orEmpty(), it.id) }.map {
                ShareContact(
                    userId = it.id,
                    title = it.name,
                    avatar = it.avatar
                )
            }
            val newData = value.data.orEmpty() + contacts
            _uiStateFlow.emit(UiState(data = newData))
        } else {
            noMoreContact = true
            _uiStateFlow.emit(value.copy(error = result.exceptionOrNull(), loading = false))
        }
    }

    private fun checkHasContact(contacts: List<ShareContact>, userId: String): Boolean {
        return contacts.any { it.userId == userId }
    }
}
