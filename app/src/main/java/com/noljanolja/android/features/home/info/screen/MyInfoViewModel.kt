package com.noljanolja.android.features.home.info.screen

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
class MyInfoViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val authSdk: AuthSdk,
) : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<MyInfoUIState>(MyInfoUIState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            authSdk.getCurrentUser().collectLatest {
                _uiStateFlow.emit(MyInfoUIState.Loaded(it))
            }
        }
    }

    fun handleEvent(event: MyInfoEvent) {
        launch {
            when (event) {
                MyInfoEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                MyInfoEvent.Logout -> {
                    authSdk.logOut()
                    navigationManager.navigate(NavigationDirections.Home)
                }
            }
        }
    }
}

sealed interface MyInfoUIState {
    object Loading : MyInfoUIState
    data class Loaded(val user: AuthUser?) : MyInfoUIState
}
