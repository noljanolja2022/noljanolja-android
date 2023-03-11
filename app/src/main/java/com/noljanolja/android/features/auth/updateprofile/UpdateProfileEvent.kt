package com.noljanolja.android.features.auth.updateprofile

import com.noljanolja.core.user.domain.model.Gender
import kotlinx.datetime.LocalDate

sealed interface UpdateProfileEvent {
    data class UploadAvatar(
        val avatar: ByteArray,
    ) : UpdateProfileEvent

    data class Update(
        val name: String,
        val dob: LocalDate?,
        val gender: Gender?,
    ) : UpdateProfileEvent

    object DismissError : UpdateProfileEvent
}