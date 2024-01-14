package com.noljanolja.android.features.home.sendpoint

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.error.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.user.data.model.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 1/2/2024.
 */

class SendPointViewModel(
    private val friendId: String = ""
) : BaseViewModel() {
    private val _checkPointValid: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    internal val checkPointValid = _checkPointValid.asStateFlow()

    private val _sendSuccessEvent: MutableSharedFlow<Boolean?> = MutableStateFlow(null)
    val sendSuccessEvent = _sendSuccessEvent.asSharedFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    fun handleEvent(event: SendPointEvent) {
        viewModelScope.launch {
            when (event) {
                SendPointEvent.Back -> navigationManager.navigate(NavigationDirections.Back)

                SendPointEvent.HideDialog -> _checkPointValid.emit(null)

                is SendPointEvent.CheckValidPoint -> {
                    event.point?.let {
                        if (it > 0) {
                            _checkPointValid.emit(
                                it <= memberInfoFlow.value.point || event.isRequestPoint
                            )
                        }
                    }
                }

                is SendPointEvent.SendPoint -> {
                    event.run {
                        sendPoint(
                            point = point,
                            isRequestPoint = isRequestPoint
                        )
                    }
                }
            }
        }
    }

    private suspend fun sendPoint(point: Long?, isRequestPoint: Boolean) {
        point?.let {
            _isLoading.emit(true)
            val result = coreManager.sendPoint(
                SendPointRequest(
                    isRequestPoint = isRequestPoint,
                    toUserId = friendId,
                    points = it
                )
            )
            updateUserState()
            _isLoading.emit(false)
            if (result.isSuccess) {
                _sendSuccessEvent.emit(isRequestPoint)
            } else {
                sendError(result.exceptionOrUnDefined())
            }
        }
    }
}
