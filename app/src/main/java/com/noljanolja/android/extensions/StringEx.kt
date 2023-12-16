package com.noljanolja.android.extensions

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
