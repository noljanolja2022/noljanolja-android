package com.noljanolja.android.util

import android.content.Context
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun formatPhoneNumber(phoneNumber: String?, context: Context): String {
    return try {
        val countryCode = getCountryCode(context).uppercase()
        val phoneUtil = PhoneNumberUtil.getInstance()
        val numberProto = phoneUtil.parse(phoneNumber, countryCode)
        if (phoneUtil.isValidNumber(numberProto)) {
            phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } else {
            phoneNumber
        }
    } catch (e: Exception) {
        phoneNumber
    }.orEmpty()
}

fun getCountryCode(context: Context): String {
    val countryCodeValue =
        (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkCountryIso
    return countryCodeValue ?: "KR"
}