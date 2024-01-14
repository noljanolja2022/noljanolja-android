package com.noljanolja.android.features.home.friend_notification

import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.error.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.Timer.MILLISECOND_OF_ONE_DAY
import com.noljanolja.core.contacts.domain.model.*
import com.noljanolja.core.user.data.model.request.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 1/14/2024.
 */

class FriendNotificationViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(FriendNotificationUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        initData()
    }

    private fun initData() {
        launch {
            _isLoading.emit(true)
            val result = coreManager.getNotifications(
                GetNotificationsRequest()
            )
            _isLoading.emit(false)

            if (result.isSuccess) {
                val data = result.getOrDefault(emptyList()).map {
                    it.apply {
                        timeDisplay = createdAt.getDistanceMillisecond()
                            .takeIf { dis -> dis < MILLISECOND_OF_ONE_DAY }
                    }
                }
                _uiStateFlow.emit(
                    FriendNotificationUiState(
                        notificationsRead = data.filter { it.isRead },
                        notificationsUnRead = data.filter { !it.isRead },
                    )
                )
            } else {
                sendError(result.exceptionOrUnDefined())
            }
        }
    }

    fun handleEvent(event: FriendNotificationEvent) {
        launch {
            when (event) {
                FriendNotificationEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
            }
        }
    }
}

data class FriendNotificationUiState(
    val notificationsRead: List<NotificationData> = emptyList(),
    val notificationsUnRead: List<NotificationData> = emptyList(),
)
