package com.noljanolja.android.features.splash

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(SplashUiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val user = coreManager.getCurrentUser(true).getOrNull()
            user?.let {
                if (user.name.isNullOrBlank()) {
                    navigationManager.navigate(
                        NavigationDirections.UpdateProfile
                    )
                } else {
                    navigationManager.navigate(
                        NavigationDirections.Home
                    )
                }
            } ?: _uiStateFlow.emit(SplashUiState(loading = false))
        }
    }

    fun handleEvent(event: SplashEvent) {
        launch {
            when (event) {
                is SplashEvent.Continue -> navigationManager.navigate(
                    NavigationDirections.TermsOfService
                )
            }
        }
    }
}

data class SplashUiState(
    val loading: Boolean = false,
)