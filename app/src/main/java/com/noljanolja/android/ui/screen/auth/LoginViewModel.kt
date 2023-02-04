package com.noljanolja.android.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.noljanolja.android.ui.screen.base.launch
import com.noljanolja.android.ui.screen.navigation.NavigationDirections
import com.noljanolja.android.ui.screen.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {
    fun handleEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.GoToMain -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            }
        }
    }
}