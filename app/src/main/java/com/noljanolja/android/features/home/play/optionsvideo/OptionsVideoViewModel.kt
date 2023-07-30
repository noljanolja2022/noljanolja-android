package com.noljanolja.android.features.home.play.optionsvideo

import com.noljanolja.android.common.base.BaseShareContactViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.exceptionOrUnDefined
import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.video.domain.model.Video

class OptionsVideoViewModel : BaseShareContactViewModel() {
    fun handleEvent(event: OptionsVideoEvent) {
        launch {
            when (event) {
                is OptionsVideoEvent.ShareVideo -> shareVideo(
                    video = event.video,
                    shareContact = event.shareContact
                )
            }
        }
    }

    private suspend fun shareVideo(video: Video, shareContact: ShareContact) {
        val sendMessage = Message(
            message = video.title,
            type = MessageType.PLAINTEXT
        )
        shareContact.conversationId?.let {
            val result = coreManager.sendConversationsMessage(
                conversationIds = listOf(shareContact.conversationId),
                message = sendMessage,
                userIds = emptyList(),
                shareVideoId = video.id
            )
            if (result.isSuccess) {
                _shareSuccessEvent.emit(Unit)
            } else {
                sendError(result.exceptionOrUnDefined())
            }
            return
        }
        shareContact.userId?.let {
            val me = coreManager.getCurrentUser().getOrNull()
            val result = coreManager.sendConversationsMessage(
                conversationIds = listOf(0L),
                userIds = listOfNotNull(it, me?.id),
                message = sendMessage,
                shareVideoId = video.id,
                title = shareContact.title
            )
            if (result.isSuccess) {
                _shareSuccessEvent.emit(Unit)
            } else {
                sendError(result.exceptionOrUnDefined())
            }
            return
        }
    }
}