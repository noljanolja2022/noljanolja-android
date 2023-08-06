package com.noljanolja.android.features.home

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.event.domain.model.EventBanner
import com.noljanolja.core.user.domain.model.CheckinProgress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckinViewModel : BaseViewModel() {
    private val _checkinSuccessEvent = MutableSharedFlow<String>()
    val checkinSuccessEvent = _checkinSuccessEvent.asSharedFlow()
    private val _checkinProgressesFlow = MutableStateFlow<List<CheckinProgress>>(emptyList())
    val checkinProgressFlow = _checkinProgressesFlow.asStateFlow()

    private val _eventBannersFlow = MutableStateFlow<List<EventBanner>>(emptyList())
    val eventBannersFlow = _eventBannersFlow.asStateFlow()

    init {
        launch {
            coreManager.getCheckinProgress().getOrDefault(emptyList()).let {
                _checkinProgressesFlow.emit(it)
            }
        }
    }

    fun handleEvent(event: CheckinEvent) {
        launch {
            when (event) {
                CheckinEvent.Back -> back()
                CheckinEvent.Checkin -> checkin()
                CheckinEvent.Referral -> navigationManager.navigate(NavigationDirections.Referral)
            }
        }
    }

    private suspend fun checkin() {
        val result = coreManager.checkin()
        if (result.isSuccess) {
            val checkinProgresses = coreManager.getCheckinProgress().getOrDefault(emptyList())
            _checkinProgressesFlow.emit(checkinProgresses)
            _checkinSuccessEvent.emit(result.getOrDefault(""))
        } else {
            result.exceptionOrNull()?.let {
                sendError(it)
            }
        }
    }
}