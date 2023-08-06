package com.noljanolja.android.features.auth.updateprofile

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.util.formatTime
import com.noljanolja.core.user.domain.model.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdateProfileViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UpdateProfileUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: UpdateProfileEvent) {
        launch {
            when (event) {
                UpdateProfileEvent.DismissError -> {
                    _uiStateFlow.emit(UpdateProfileUiState())
                }

                is UpdateProfileEvent.Update -> update(
                    event.name,
                    event.gender,
                    event.dob?.formatTime("MMMM yyyy"),
                    event.fileName,
                    event.fileType,
                    event.files,
                )
            }
        }
    }

    private suspend fun update(
        name: String,
        gender: Gender?,
        dob: String?,
        fileName: String?,
        fileType: String = "",
        files: ByteArray?,
    ) {
        _uiStateFlow.emit(UpdateProfileUiState(loading = true))
        if (fileName != null && files != null) {
            val updateAvatarResult =
                coreManager.updateAvatar(name = fileName, type = fileType, files = files)
        }
        val result = coreManager.updateUser(name = name, email = null)
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