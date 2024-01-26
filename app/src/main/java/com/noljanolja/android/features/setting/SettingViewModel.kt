package com.noljanolja.android.features.setting

import androidx.lifecycle.*
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.UnexpectedFailure
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.util.showToast
import com.noljanolja.core.file.model.*
import com.noljanolja.core.loyalty.domain.model.*
import kotlinx.coroutines.flow.*

class SettingViewModel(
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(SettingUIState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _updateUserEvent = MutableSharedFlow<Boolean>()
    val updateUserEvent = _updateUserEvent.asSharedFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    init {
        launch {
            _uiStateFlow.emit(
                SettingUIState(
                    allowPushNotification = sharedPreferenceHelper.pushNotification
                )
            )
        }
    }

    fun handleEvent(event: SettingEvent) {
        launch {
            when (event) {
                SettingEvent.Back -> {
                    navigationManager.navigate(NavigationDirections.Back)
                }

                SettingEvent.ShowLicense -> {

                }

                SettingEvent.TogglePushNotification -> {
                    sharedPreferenceHelper.pushNotification =
                        !sharedPreferenceHelper.pushNotification
                    with(_uiStateFlow) {
                        emit(value.copy(allowPushNotification = sharedPreferenceHelper.pushNotification))
                    }
                }

                SettingEvent.Logout -> {
                    val result = coreManager.logout()
                    if (result.getOrNull() == true) {
                        navigationManager.navigate(NavigationDirections.Auth)
                    } else {
                        context.showToast(
                            result.exceptionOrNull()?.message ?: UnexpectedFailure.message
                        )
                    }
                }

                SettingEvent.FAQ -> {
                    navigationManager.navigate(NavigationDirections.FAQ)
                }

                SettingEvent.Licence -> {
                    navigationManager.navigate(NavigationDirections.Licenses)
                }

                is SettingEvent.ChangeAvatar -> updateAvatar(event.fileInfo)
            }
        }
    }

    private suspend fun updateAvatar(file: FileInfo) {
        launch {
            val result = coreManager.updateAvatar(
                name = file.name,
                type = file.contentType,
                files = file.contents
            )
            if (result.isSuccess) {
                updateUserState()
                _updateUserEvent.emit(true)
            } else {
                result.exceptionOrNull()?.let {
                    sendError(it)
                }
            }
        }
    }
}

data class SettingUIState(
    val versionName: String = BuildConfig.VERSION_NAME,
    val allowPushNotification: Boolean = false,
    val memberInfo: MemberInfo? = null
)
