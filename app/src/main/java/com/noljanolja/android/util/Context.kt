package com.noljanolja.android.util

import android.content.Context
import android.widget.Toast
import com.noljanolja.android.R
import com.noljanolja.android.common.error.LoginEmailPasswordFailed

fun Context.showToast(
    text: String?,
    time: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, text, time).show()

fun Context.getErrorMessage(error: Throwable) = when (error) {
    is LoginEmailPasswordFailed -> getString(R.string.failure_login_email_password_error)
    else -> error.message.orEmpty()
}