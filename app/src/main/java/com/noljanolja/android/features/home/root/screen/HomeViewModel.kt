package com.noljanolja.android.features.home.root.screen

import com.d2brothers.firebase_auth.AuthSdk
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
    private val authSdk: AuthSdk,
    private val navigationManager: NavigationManager,
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
        onChange: () -> Unit,
    ) {
        launch {
            if (item == HomeNavigationItem.CelebrationItem) {
                onChange.invoke()
                return@launch
            }
            val user = authSdk.getCurrentUser(true).first()
            if (user?.isVerify == true) {
                onChange.invoke()
            } else {
                _showRequireLoginPopupEvent.emit(true)
            }
        }
    }

    private fun loginOrVerifyEmail() {
        launch {
            val user = authSdk.getCurrentUser().first()
            when {
                // TODO : Check verify if need after
                true -> {
                    _showRequireLoginPopupEvent.emit(false)
                    navigationManager.navigate(NavigationDirections.LoginOrSignup)
                }
                !user!!.isVerify -> sendError(Throwable("Verify fail"))
                else -> _showRequireLoginPopupEvent.emit(false)
            }
        }
    }
}
