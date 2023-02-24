package com.noljanolja.android.features.splash.screen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    init {
        launch {
            val user = userRepository.getCurrentUser() ?: userRepository.getMe().getOrNull()
            navigationManager.navigate(
                NavigationDirections.Home.takeIf { user != null }
                    ?: NavigationDirections.LoginOrSignup
            )
        }
    }
}
