package com.noljanolja.android.features.auth.updateprofile

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.util.Constant.LocaleDateTime.FROM_SERVER
import com.noljanolja.android.util.formatTime
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.domain.model.Gender
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdateProfileViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UpdateProfileUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow = _userFlow.asStateFlow()

    init {
        launch {
            coreManager.getCurrentUser().getOrNull()?.let {
                _userFlow.emit(it)
            }
        }
    }

    fun handleEvent(event: UpdateProfileEvent) {
        launch {
            when (event) {
                UpdateProfileEvent.DismissError -> {
                    _uiStateFlow.emit(UpdateProfileUiState())
                }

                is UpdateProfileEvent.Update -> event.run {
                    update(
                        name = name,
                        gender = gender,
                        dob = dob?.formatTime(FROM_SERVER),
                        phone = phone,
                        email = email,
                        fileName = fileName,
                        fileType = fileType,
                        files = files,
                    )
                }

                UpdateProfileEvent.OpenCountryList -> {
                    navigationManager.navigate(NavigationDirections.CountryPicker)
                }
            }
        }
    }

    private suspend fun update(
        name: String,
        gender: Gender?,
        dob: String?,
        phone: String?,
        email: String?,
        fileName: String?,
        fileType: String = "",
        files: ByteArray?,
    ) {
        _uiStateFlow.emit(UpdateProfileUiState(loading = true))
        if (fileName != null && files != null) {
            val updateAvatarResult =
                coreManager.updateAvatar(name = fileName, type = fileType, files = files)
        }
        val result = coreManager.updateUser(
            UpdateUserRequest(
                name = name,
                email = email,
                phone = phone,
                gender = gender,
                dob = dob,
            )
        )
        if (result.isSuccess) {
            _uiStateFlow.emit(UpdateProfileUiState())
            navigationManager.navigate(NavigationDirections.AddReferral)
        } else {
            _uiStateFlow.emit(UpdateProfileUiState(error = result.exceptionOrNull()))
        }
    }
}

data class UpdateProfileUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
)