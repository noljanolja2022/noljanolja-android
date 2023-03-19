package com.noljanolja.android.features.splash

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class SplashViewModel : BaseViewModel() {
    private val authSdk: AuthSdk by inject()
    private val _uiStateFlow = MutableStateFlow(SplashUiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            authSdk.getIdToken(true)?.let {
                coreManager.saveAuthToken(it)
                val user = coreManager.getCurrentUser(true).getOrNull() ?: return@let
                coreManager.pushToken()
                if (user.name.isBlank()) {
                    navigationManager.navigate(
                        NavigationDirections.UpdateProfile
                    )
                } else {
                    navigationManager.navigate(
                        NavigationDirections.Home
                    )
                }
            }
            _uiStateFlow.emit(SplashUiState(loading = false))
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