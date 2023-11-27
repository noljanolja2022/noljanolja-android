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
    var loadingTime = 0

    init {
        reload()
    }

    fun handleEvent(event: SplashEvent) {
        launch {
            when (event) {
                is SplashEvent.Continue -> {
                    if (_uiStateFlow.value.needReload) {
                        reload()
                    } else {
                        navigationManager.navigate(
                            NavigationDirections.TermsOfService
                        )
                    }
                }
            }
        }
    }

    private fun reload() {
        launch {
            loadingTime++
            _uiStateFlow.emit(SplashUiState(loading = true))
            authSdk.getIdToken(true)?.let {
                coreManager.saveAuthToken(it)
                coreManager.getCurrentUser(true).getOrNull()?.let { user ->
                    coreManager.pushToken()
                    if (user.name.isBlank() || user.phone.isNullOrBlank()) {
                        navigationManager.navigate(
                            NavigationDirections.UpdateProfile
                        )
                    } else {
                        navigationManager.navigate(
                            NavigationDirections.UpdateProfile
                        )
                    }
                }
            } ?: let {
                coreManager.logout(requireSuccess = false)
                _uiStateFlow.emit(SplashUiState(loading = false))
            }
        }
    }
}

data class SplashUiState(
    val loading: Boolean = false,
    val progress: Int = 0,
    val needReload: Boolean = false,
)