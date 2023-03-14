package com.noljanolja.core.utils

import android.content.Context
import com.noljanolja.core.db.Noljanolja
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(Noljanolja.Schema, context, "Noljanolja")
}