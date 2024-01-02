package com.noljanolja.android.features.home.sendpoint

/**
 * Created by tuyen.dang on 1/2/2024.
 */

sealed interface SendPointEvent {
    object Back : SendPointEvent

    object HideDialog : SendPointEvent

    data class CheckValidPoint(
        val point: Long?,
        val isRequestPoint: Boolean
    ) : SendPointEvent

    data class SendPoint(
        val point: Long?,
        val isRequestPoint: Boolean
    ) : SendPointEvent
}
