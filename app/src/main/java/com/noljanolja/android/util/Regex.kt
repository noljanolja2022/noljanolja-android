package com.noljanolja.android.util

object RegexExt {
    private const val EMAIL_REGEX = "^[A-Za-z\\d](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }
}
