package com.noljanolja.android.features.home.mypage

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyPageViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<MyPageUIState>(MyPageUIState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiStateFlow.emit(MyPageUIState.Loaded(coreManager.getCurrentUser().getOrNull()))
        }
    }

    fun handleEvent(event: MyPageEvent) {
        launch {
            when (event) {
                is MyPageEvent.GoToMyInfo -> {
                    navigationManager.navigate(NavigationDirections.MyInfo)
                }
            }
        }
    }
}

sealed interface MyPageUIState {
    object Loading : MyPageUIState
    data class Loaded(val user: User?) : MyPageUIState
}
