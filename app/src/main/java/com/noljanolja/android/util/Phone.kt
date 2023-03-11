package com.noljanolja.android.util

import android.content.Context
import android.telephony.PhoneNumberUtils

fun String.formatPhone(context: Context): String {
    val phone = try {
        PhoneNumberUtils.formatNumberToE164(
            this,
            context.getLocalLocale().uppercase()
        ) ?: this
    } catch (e: Exception) {
        this
    }
    return phone
}