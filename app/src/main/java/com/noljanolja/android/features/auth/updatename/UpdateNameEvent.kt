package com.noljanolja.android.features.auth.updatename

/**
 * Created by tuyen.dang on 1/28/2024.
 */

interface UpdateNameEvent {
    object Back : UpdateNameEvent

    data class UpdateName(val name: String) : UpdateNameEvent
}
