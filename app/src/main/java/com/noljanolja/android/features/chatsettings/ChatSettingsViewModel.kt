package com.noljanolja.android.features.chatsettings

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.file.model.FileInfo
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ChatSettingsViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<ChatSettingsUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _updateUserEvent = MutableSharedFlow<Boolean>()
    val updateUserEvent = _updateUserEvent.asSharedFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    init {
        launch {
            _uiStateFlow.emit(UiState(loading = true))
            val user = coreManager.getCurrentUser().getOrDefault(User())
            _uiStateFlow.emit(
                UiState(
                    data = ChatSettingsUiData(
                        user = user,
                    )
                )
            )
        }
    }

    fun handleEvent(event: ChatSettingsEvent) {
        launch {
            when (event) {
                ChatSettingsEvent.Back -> back()
                is ChatSettingsEvent.ChangeAvatar -> updateAvatar(event.fileInfo)
            }
        }
    }

    private suspend fun updateAvatar(file: FileInfo) {
        launch {
            val result = coreManager.updateAvatar(
                name = file.name,
                type = file.contentType,
                files = file.contents
            )
            if (result.isSuccess) {
                coreManager.getCurrentUser(forceRefresh = true, onlyLocal = false)
                _updateUserEvent.emit(true)
            } else {
                result.exceptionOrNull()?.let {
                    sendError(it)
                }
            }
        }
    }
}

data class ChatSettingsUiData(
    val user: User = User(),
)