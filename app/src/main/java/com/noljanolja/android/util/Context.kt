package com.noljanolja.android.util

import android.content.Context
import android.widget.Toast
import com.noljanolja.android.R
import com.noljanolja.android.common.error.ValidEmailFailed

fun Context.showToast(
    text: String?,
    time: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, text, time).show()

fun Context.getErrorMessage(error: Throwable) = when (error) {
    is ValidEmailFailed -> getString(R.string.invalid_email_format)
    else -> error.message.orEmpty()
}
