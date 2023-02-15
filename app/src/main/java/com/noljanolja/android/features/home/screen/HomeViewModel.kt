package com.noljanolja.android.features.home.screen

import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager
) : BaseViewModel() {
    private val _showRequireLoginPopupEvent = MutableSharedFlow<Boolean>()
    val showRequireLoginPopupEvent = _showRequireLoginPopupEvent.asSharedFlow()

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeNavigationItem -> changeNavigationItem(event.item, event.onChange)
            HomeEvent.LoginOrVerifyEmail -> loginOrVerifyEmail()
        }
    }

    private fun changeNavigationItem(
        item: HomeNavigationItem,
        onChange: () -> Unit
    ) {
        launch {
            // Test Logout
            if (item == HomeNavigationItem.UserItem) {
                sendError(Throwable("Logout Success! This button test logout"))
                logOut()
                return@launch
            }
            if (item == HomeNavigationItem.HomeItem) {
                onChange.invoke()
                return@launch
            }
            val user = authRepository.getCurrentUser().first()
            if (user?.isVerify == true) {
                onChange.invoke()
            } else {
                _showRequireLoginPopupEvent.emit(true)
            }
        }
    }

    private fun loginOrVerifyEmail() {
        launch {
            val user = authRepository.getCurrentUser().first()
            when {
                user == null -> {
                    _showRequireLoginPopupEvent.emit(false)
                    navigationManager.navigate(NavigationDirections.LoginOrSignup)
                }
                !user.isVerify -> sendError(Throwable("Verify fail"))
                else -> _showRequireLoginPopupEvent.emit(false)
            }

        }
    }

    fun logOut() {
        launch {
            val result = authRepository.logOut()
            if (result.isSuccess) {
                navigationManager.navigate(NavigationDirections.Home)
            }
        }
    }
}
