package com.noljanolja.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.Coil
import coil.memory.MemoryCache
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.error.ValidEmailFailed
import com.noljanolja.core.file.model.FileInfo
import okio.Path.Companion.toPath
import java.io.File
import java.io.FileOutputStream

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

fun Context.openImageFromCache(key: String) {
    val tmpFile = File.createTempFile("temp_photo", ".png", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    val cache = Coil.imageLoader(this).memoryCache
    val bitmap = cache?.get(MemoryCache.Key(key))?.bitmap ?: return
    val out = FileOutputStream(tmpFile)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    out.flush()
    out.close()
    val imageUri = FileProvider.getUriForFile(
        this,
        "${BuildConfig.APPLICATION_ID}.provider",
        tmpFile
    )
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(imageUri, "image/*")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(intent)
}