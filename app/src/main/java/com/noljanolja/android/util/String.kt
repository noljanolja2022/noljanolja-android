package com.noljanolja.android.util

import kotlinx.datetime.toKotlinInstant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.datetime.Instant as KInstant

fun String.toInstant(): KInstant {
    val formatter = DateTimeFormatter.ofPattern("MM-yyyy")
    val localDate = LocalDate.parse(this, formatter)
    val instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC)
    return instant.toKotlinInstant()
}

fun String.capitalizeFirstLetter(): String {
    if (this.isEmpty()) {
        return this
    }
    val firstChar = this[0]
    val capitalizedFirstChar = firstChar.uppercaseChar()
    val remainingChars = this.substring(1)
    return capitalizedFirstChar + remainingChars.lowercase()
}