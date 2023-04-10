package com.noljanolja.android.features.edit_chat_title

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections

class EditChatTitleViewModel(
    private val conversationId: Long,
) : BaseViewModel() {

    fun handleEvent(event: EditChatTitleEvent) {
        launch {
            when (event) {
                EditChatTitleEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                is EditChatTitleEvent.ConfirmEditChatTitle -> {
                    updateConversation(event.name)
                }
            }
        }
    }

    private suspend fun updateConversation(title: String) {
        val result = coreManager.updateConversation(
            conversationId = conversationId,
            title = title
        )
        if (result.isSuccess) {
            navigationManager.navigate(NavigationDirections.Back)
        }
    }
}