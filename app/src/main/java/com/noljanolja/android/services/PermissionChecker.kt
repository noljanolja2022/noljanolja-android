package com.noljanolja.android.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class PermissionChecker(
    private val context: Context,
) {
    fun canReadContacts(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun canReadExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            IMAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun canOpenCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val IMAGE_PERMISSION = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_MEDIA_IMAGES
        }
    }
}