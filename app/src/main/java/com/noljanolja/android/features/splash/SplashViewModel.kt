package com.noljanolja.android.features.splash

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.core.CoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val coreManager: CoreManager,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(SplashUiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val user = coreManager.getCurrentUser().getOrNull()
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