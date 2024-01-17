package com.noljanolja.android.features.auth.updateprofile

import com.noljanolja.core.user.domain.model.Gender
import kotlinx.datetime.LocalDate

sealed interface UpdateProfileEvent {
    data class Update(
        val name: String,
        val phone: String?,
        val email: String?,
        val dob: LocalDate?,
        val gender: Gender?,
        val fileName: String? = null,
        val fileType: String = "",
        val files: ByteArray? = null,
    ) : UpdateProfileEvent

    object DismissError : UpdateProfileEvent
    object OpenCountryList : UpdateProfileEvent
}