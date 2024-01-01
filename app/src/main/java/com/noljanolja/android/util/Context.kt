package com.noljanolja.android.util

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.*
import android.widget.Toast
import androidx.core.content.*
import co.touchlab.kermit.Logger
import coil.Coil
import coil.memory.MemoryCache
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.data.*
import com.noljanolja.android.common.error.PhoneNotAvailableFailure
import com.noljanolja.android.common.error.QrNotValidFailure
import com.noljanolja.android.common.error.ValidEmailFailure
import com.noljanolja.android.common.error.ValidPhoneFailure
import com.noljanolja.android.util.Constant.PackageShareToApp.MESSAGE_APP_PACKAGE
import com.noljanolja.core.file.model.FileInfo
import okio.Path.Companion.toPath
import java.io.*
import java.util.*


fun Context.showToast(
    text: String?,
    time: Int = Toast.LENGTH_LONG,
) = Toast.makeText(this, text, time).show()

fun Context.showError(error: Throwable, time: Int = Toast.LENGTH_LONG) {
    Logger.e("Context show Error: $error")
    Toast.makeText(this, getErrorMessage(error), time).show()
}

fun Context.getErrorMessage(error: Throwable) = when (error) {
    is ValidEmailFailure -> getString(R.string.invalid_email_format)
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

fun Context.loadFileInfo(uri: Uri?): FileInfo? {
    if (uri == null) return null
    return try {
        val contents = contentResolver.openInputStream(uri)!!.readBytes()
        val type = getType(uri)
        val name = getName(uri)
        FileInfo(name, uri.toString().toPath(false), type, contents)
    } catch (e: Throwable) {
        null
    }
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
    val imageUri = getUriFromCache(key)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(imageUri, "image/*")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(intent)
}

fun Context.getUriFromCache(key: String): Uri? {
    val tmpFile = File.createTempFile("temp_photo", ".png", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    val cache = Coil.imageLoader(this).memoryCache
    val bitmap = cache?.get(MemoryCache.Key(key))?.bitmap ?: return null
    val out = FileOutputStream(tmpFile)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    out.flush()
    out.close()
    return FileProvider.getUriForFile(
        this,
        "${BuildConfig.APPLICATION_ID}.provider",
        tmpFile
    )
}

fun Context.downloadFromUrl(url: String): Boolean {
    val context = this
    return try {
        val cache = Coil.imageLoader(context).memoryCache
        val bitmap = cache?.get(MemoryCache.Key(url))?.bitmap ?: return false
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath,
            "${UUID.randomUUID()}.jpg"
        ).apply {
            createNewFile()
        }
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun Context.createImageFromCache(key: String): Boolean {
    val tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".png", filesDir).apply {
        createNewFile()
    }
    val cache = Coil.imageLoader(this).memoryCache
    val bitmap = cache?.get(MemoryCache.Key(key))?.bitmap ?: return false
    val out = FileOutputStream(tmpFile)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    return true
}

fun Context.openUrl(url: String) {
    val uri = Uri.parse(url)
    if (!uri.scheme.isNullOrEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } else {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$url"))
        startActivity(intent)
    }
}

fun Context.getErrorDescription(error: Throwable): String {
    return when (error) {
        ValidPhoneFailure -> getString(R.string.error_phone_invalid)
        PhoneNotAvailableFailure -> getString(R.string.error_phone_is_not_available)
        QrNotValidFailure -> getString(R.string.error_qr_not_valid)
        else -> error.message?.takeIf { it.isNotBlank() } ?: getString(R.string.error_unexpected)
    }
}

fun Context.shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun Context.copyToClipboard(text: String) {
    val clipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("copy", text)
    clipboardManager.setPrimaryClip(clip)
}

fun Context.shareToAnotherApp(videoUrl: String, shareToAppData: ShareToAppData) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, videoUrl)
        if(shareToAppData.packageName == MESSAGE_APP_PACKAGE) {
            shareToAppData.packageName = Telephony.Sms.getDefaultSmsPackage(this@shareToAnotherApp)
        }
        setPackage(shareToAppData.packageName)// Replace with the actual Facebook app package name
    }
    if (intent.resolveActivity(packageManager) == null) {
        showToast(getString(R.string.common_install_app, shareToAppData.appName))
    } else {
        try {
            startActivity(intent)
        } catch (_: Exception) {
        }
    }
}

fun Context.getClientId() = if (BuildConfig.DEBUG) {
    "954965503519-4indv4tab6cr141999er2dd8nj43mtcr.apps.googleusercontent.com"
} else {
    "954965503519-2hbq9cb0ojtb6o08k3b54itvb5bq56bk.apps.googleusercontent.com"
}