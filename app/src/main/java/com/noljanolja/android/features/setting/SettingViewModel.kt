package com.noljanolja.android.features.setting

import com.noljanolja.android.BuildConfig
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(SettingUIState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

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
            }
        }
    }
}

data class SettingUIState(
    val versionName: String = BuildConfig.VERSION_NAME,
    val allowPushNotification: Boolean = false,
)
