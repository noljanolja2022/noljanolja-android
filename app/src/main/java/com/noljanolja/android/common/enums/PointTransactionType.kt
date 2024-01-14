package com.noljanolja.android.common.enums

/**
 * Created by tuyen.dang on 1/14/2024.
 */

enum class PointTransactionType(val type: String) {
    REQUEST("REQUEST_POINT"),
    SEND("SEND_POINT");

    companion object {
        internal fun isRequestPoint(value: String): Boolean = value == REQUEST.type
    }
}