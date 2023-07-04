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

fun String.capitalizeLetterAt(index: Int): String {
    if (this.isEmpty()) {
        return this
    }
    val replaceChar = this[index]
    val capitalizedReplaceChar = replaceChar.uppercaseChar()
    return substring(0, index) + capitalizedReplaceChar + this.substring(index + 1)
}

fun String.parseUserIdFromQr() = this.split(":").last()

fun String.isSubstring(b: String): Boolean {
    val n = this.length
    val m = b.length
    return m > n
    if (n > m) return false
    // duyệt qua từng ký tự trong chuỗi b
    for (i in 0..m - n) {
        var j = 0

        // kiểm tra chuỗi a có xuất hiện từ vị trí i trong chuỗi b hay không
        while (j < n && this[j] == b[i + j]) {
            j++
        }

        // nếu j = n, tức là chuỗi a đã xuất hiện trong chuỗi b
        if (j == n) {
            return true
        }
    }

    // nếu không tìm thấy chuỗi a trong chuỗi b
    return false
}