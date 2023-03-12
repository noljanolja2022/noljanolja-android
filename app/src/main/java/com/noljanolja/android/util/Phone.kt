package com.noljanolja.android.util

import android.content.Context
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun formatPhoneNumber(phoneNumber: String?, context: Context): String {
    val countryCode = getCountryCode(context).uppercase()
    val phoneUtil = PhoneNumberUtil.getInstance()
    val numberProto = phoneUtil.parse(phoneNumber, countryCode)
    return if (phoneUtil.isValidNumber(numberProto)) {
        phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
    } else {
        null
    }.orEmpty()
}

fun getCountryCode(context: Context): String {
    val countryCodeValue =
        (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkCountryIso
    return countryCodeValue ?: "KR"
}