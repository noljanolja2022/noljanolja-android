package com.noljanolja.android.features.auth.updatename

import com.noljanolja.android.common.base.*
import com.noljanolja.core.user.data.model.request.*

/**
 * Created by tuyen.dang on 1/28/2024.
 */

class UpdateNameViewModel : BaseViewModel() {
    fun handleEvent(event: UpdateNameEvent) {
        launch {
            when (event) {
                UpdateNameEvent.Back -> navigationManager.back()

                is UpdateNameEvent.UpdateName -> updateName(event.name)
            }
        }
    }

    private suspend fun updateName(name: String) {
        _isLoading.emit(true)
        val result = coreManager.updateUser(
            userStateFlow.value.run {
                UpdateUserRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    dob = dob?.toString(),
                )
            }
        )
        updateUserState()
        if (result.isSuccess) {
            navigationManager.back()
        } else {
            result.exceptionOrNull()?.let {
                sendError(it)
            }
        }
        _isLoading.emit(false)
    }
}
