package com.noljanolja.android.features.home.info

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<MyInfoUIState>(MyInfoUIState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiStateFlow.emit(MyInfoUIState.Loaded(userRepository.getCurrentUser().getOrNull()))
        }
    }

    fun handleEvent(event: MyInfoEvent) {
        launch {
            when (event) {
                MyInfoEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }
                MyInfoEvent.Logout -> {
                    userRepository.logout()
                    navigationManager.navigate(NavigationDirections.Auth)
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
