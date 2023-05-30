package com.noljanolja.android.features.chatsettings

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.features.home.wallet.WalletUIData
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatSettingsViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<ChatSettingsUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val user = coreManager.getCurrentUser().getOrDefault(User())
            val memberInfo = coreManager.getMemberInfo().getOrDefault(MemberInfo())
            _uiStateFlow.emit(
                UiState(
                    data = ChatSettingsUiData(
                        user = user,
                        memberInfo = memberInfo
                    )
                )
            )
        }
    }

    fun handleEvent(event: ChatSettingsEvent) {
        launch {
            when (event) {
                ChatSettingsEvent.Back -> back()
            }
        }
    }
}

data class ChatSettingsUiData(
    val user: User,
    val memberInfo: MemberInfo
)