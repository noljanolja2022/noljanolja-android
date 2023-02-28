package com.noljanolja.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.error.ValidEmailFailed
import java.io.File

fun Context.showToast(
    text: String?,
    time: Int = Toast.LENGTH_SHORT,
) = Toast.makeText(this, text, time).show()

fun Context.getErrorMessage(error: Throwable) = when (error) {
    is ValidEmailFailed -> getString(R.string.invalid_email_format)
    else -> error.message.orEmpty()
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.getTmpFileUri(name: String, ext: String): Uri {
    val tmpFile = File.createTempFile(name, ext, cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}
