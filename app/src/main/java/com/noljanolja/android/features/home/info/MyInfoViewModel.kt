package com.noljanolja.android.features.home.info

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyInfoViewModel : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<MyInfoUIState>(MyInfoUIState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiStateFlow.emit(MyInfoUIState.Loaded(coreManager.getCurrentUser().getOrNull()))
        }
    }

    fun handleEvent(event: MyInfoEvent) {
        launch {
            when (event) {
                MyInfoEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                MyInfoEvent.Logout -> {
                    if (coreManager.logout().getOrNull() == true) {
                        navigationManager.navigate(NavigationDirections.Auth)
                    }
                }
                MyInfoEvent.GoSetting -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }
            }
        }
    }
}

sealed interface MyInfoUIState {
    object Loading : MyInfoUIState
    data class Loaded(val user: User?) : MyInfoUIState
}
