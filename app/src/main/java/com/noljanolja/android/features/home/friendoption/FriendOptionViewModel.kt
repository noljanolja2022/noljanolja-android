package com.noljanolja.android.features.home.friendoption

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 11/14/2023.
 */

class FriendOptionViewModel(
    private val friendId: String = ""
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<User>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    fun handleEvent(event: FriendOptionEvent) {
        launch {
            when (event) {
                FriendOptionEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)

                FriendOptionEvent.GoToChatScreen -> {
//                    navigationManager.navigate()navigate
                }
            }
        }
    }
}
