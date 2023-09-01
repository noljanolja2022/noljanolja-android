package com.noljanolja.android.features.conversationmedia

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.conversation.domain.model.ConversationMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConversationMediaViewModel(
    private var conversationId: Long = 0L,
) : BaseViewModel() {
    private val _uiStateFlow =
        MutableStateFlow<UiState<MutableMap<ConversationMedia.AttachmentType, List<ConversationMedia>>>>(
            UiState(
                loading = true,
                data = mutableMapOf(
                    ConversationMedia.AttachmentType.PHOTO to listOf(),
                    ConversationMedia.AttachmentType.FILE to listOf(),
                    ConversationMedia.AttachmentType.LINK to listOf()
                )
            )
        )
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val pages = mutableMapOf(
        ConversationMedia.AttachmentType.PHOTO to 1,
        ConversationMedia.AttachmentType.FILE to 1,
        ConversationMedia.AttachmentType.LINK to 1,
    )

    init {
        launch {
            loadConversationMedias(ConversationMedia.AttachmentType.PHOTO)
            loadConversationMedias(ConversationMedia.AttachmentType.FILE)
            loadConversationMedias(ConversationMedia.AttachmentType.LINK)
            _uiStateFlow.emit(
                _uiStateFlow.value.copy(loading = false)
            )
        }
    }

    fun handleEvent(event: ConversationMediaEvent) {
        launch {
            when (event) {
                ConversationMediaEvent.Back -> back()
                is ConversationMediaEvent.ViewImages -> navigationManager.navigate(
                    NavigationDirections.ViewImages(images = listOf(event.image))
                )

                ConversationMediaEvent.LoadMoreFile -> loadConversationMedias(ConversationMedia.AttachmentType.FILE)
                ConversationMediaEvent.LoadMoreImage -> loadConversationMedias(ConversationMedia.AttachmentType.PHOTO)
                ConversationMediaEvent.LoadMoreLink -> loadConversationMedias(ConversationMedia.AttachmentType.LINK)
            }
        }
    }

    private suspend fun loadConversationMedias(type: ConversationMedia.AttachmentType) {
        val page = pages[type]!!
        if (page <= 0) return
        val data = _uiStateFlow.value.data
        coreManager.getConversationAttachments(
            conversationId,
            page = page,
            attachmentTypes = listOf(type)
        ).getOrDefault(emptyList()).also { medias ->
            if (page == 1) {
                data?.set(type, medias)
            } else {
                data?.set(type, data[type]!! + medias)
            }
            _uiStateFlow.value = _uiStateFlow.value.copy(data = data)
            if (medias.isNotEmpty()) {
                pages[type] = page + 1
            } else {
                pages[type] = 0
            }
        }
    }
}