package com.noljanolja.android.extensions

import com.google.gson.*
import com.google.i18n.phonenumbers.*

/**
 * Created by tuyen.dang on 11/14/2023.
 */

fun String?.convertToString(defaultValue: String = "") = this ?: defaultValue

internal inline fun <reified T> String?.parseFromJsonTo(): T? = try {
    Gson().fromJson(this, T::class.java)
} catch (e: Exception) {
    null
}

fun String?.convertToLong(defaultValue: Long = -1L) = try {
    this?.toLong() ?: defaultValue
} catch (_: Exception) {
    defaultValue
}

fun String?.getPhoneNumberFormatE164(countryCode: String) = try {
//    PhoneNumberUtils.formatNumberToE164(
//        this,
//        countryCode.uppercase()
//    )
    val result = PhoneNumberUtil.getInstance().format(
        PhoneNumberUtil.getInstance().parse(this, countryCode.uppercase()),
        PhoneNumberUtil.PhoneNumberFormat.E164
    )
    result.takeIf { result.length in 12..14 }
} catch (_: Exception) {
    null
}
