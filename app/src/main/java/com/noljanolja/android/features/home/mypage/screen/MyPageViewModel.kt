package com.noljanolja.android.features.home.mypage.screen

import androidx.lifecycle.viewModelScope
import com.d2brothers.firebase_auth.AuthSdk
import com.d2brothers.firebase_auth.model.AuthUser
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authSdk: AuthSdk,
    private val navigationManager: NavigationManager,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<MyPageUIState>(MyPageUIState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            authSdk.getCurrentUser().collectLatest {
                _uiStateFlow.emit(MyPageUIState.Loaded(it))
            }
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
    data class Loaded(val user: AuthUser?) : MyPageUIState
}
