package com.noljanolja.android.features.splash.screen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : BaseViewModel() {
    init {
        launch {
            // TODO: Keep to see splash screen
            delay(1000)
            navigationManager.navigate(
                NavigationDirections.Home,
            )
        }
    }
}
