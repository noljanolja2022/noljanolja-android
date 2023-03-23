package com.noljanolja.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.error.ValidEmailFailed
import com.noljanolja.core.file.model.FileInfo
import okio.Path.Companion.toPath
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

fun Context.loadFileInfo(uri: Uri): FileInfo {
    val contents = contentResolver.openInputStream(uri)!!.readBytes()
    val type = getType(uri)
    val name = getName(uri)
    return FileInfo(name, uri.toString().toPath(false), type, contents)
}

fun Context.getType(uri: Uri): String {
    return contentResolver.getType(uri).orEmpty()
}

fun Context.getName(uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result!!.substring(cut + 1)
        }
    }
    return result!!
}