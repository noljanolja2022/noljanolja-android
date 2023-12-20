package com.noljanolja.android.extensions

import android.telephony.*

/**
 * Created by tuyen.dang on 11/14/2023.
 */

fun String?.convertToString(defaultValue: String = "") = this ?: defaultValue

fun String?.addLine(textLine: Int, anotherLine: Int): String {
    val result = StringBuilder(this.convertToString())
    var numberOffLine = anotherLine - textLine
    while (numberOffLine > 0) {
        numberOffLine -= 1
        result.append("\n   ")
    }
    return result.toString()
}

fun String?.getPhoneNumberFormatE164(countryCode: String) = try {
    PhoneNumberUtils.formatNumberToE164(
        this,
        countryCode.uppercase()
    )
//    PhoneNumberUtil.getInstance().format(
//        PhoneNumberUtil.getInstance().parse(this,countryCode.uppercase()),
//        PhoneNumberUtil.PhoneNumberFormat.E164
//    )
} catch (_: Exception) {
    null
}
