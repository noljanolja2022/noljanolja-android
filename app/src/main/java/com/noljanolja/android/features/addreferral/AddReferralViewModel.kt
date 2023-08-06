package com.noljanolja.android.features.addreferral

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.exceptionOrUnDefined
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AddReferralViewModel : BaseViewModel() {
    private val _receivePointEvent = MutableSharedFlow<Long>()
    val receivePointEvent = _receivePointEvent.asSharedFlow()
    fun handleEvent(event: AddReferralEvent) {
        launch {
            when (event) {
                AddReferralEvent.GoToMain -> navigationManager.navigate(NavigationDirections.Home)
                is AddReferralEvent.SendCode -> sendCode(event.code)
            }
        }
    }

    private suspend fun sendCode(code: String) {
        launch {
            val result = coreManager.addReferralCode(code)
            if (result.isSuccess) {
                _receivePointEvent.emit(result.getOrDefault(0L))
            } else {
                sendError(result.exceptionOrUnDefined())
            }
        }
    }
}