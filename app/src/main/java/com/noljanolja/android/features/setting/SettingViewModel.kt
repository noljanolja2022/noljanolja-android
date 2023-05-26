package com.noljanolja.android.features.setting

import com.noljanolja.android.BuildConfig
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(SettingUIState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            coreManager.getCurrentUser(forceRefresh = false, onlyLocal = true).getOrNull()?.let {
                _uiStateFlow.emit(
                    SettingUIState(
                        user = it
                    )
                )
            }
        }
    }

    fun handleEvent(event: SettingEvent) {
        launch {
            when (event) {
                SettingEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }

                SettingEvent.ClearCacheData -> {}
                SettingEvent.ShowLicense -> {}
                SettingEvent.TogglePushNotification -> {
                    with(_uiStateFlow) {
                        emit(value.copy(allowPushNotification = !value.allowPushNotification))
                    }
                }

                SettingEvent.Logout -> {
                    if (coreManager.logout().getOrNull() == true) {
                        navigationManager.navigate(NavigationDirections.Auth)
                    }
                }

                SettingEvent.FAQ -> {
                    navigationManager.navigate(NavigationDirections.FAQ)
                }

                SettingEvent.Licence -> {
                    navigationManager.navigate(NavigationDirections.Licenses)
                }
            }
        }
    }
}

data class SettingUIState(
    val versionName: String = BuildConfig.VERSION_NAME,
    val allowPushNotification: Boolean = false,
    val user: User? = null,
)
