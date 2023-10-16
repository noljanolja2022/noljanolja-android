package com.noljanolja.android.features.auth.updateprofile.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.noljanolja.android.R
import com.noljanolja.android.util.findActivity
import com.noljanolja.android.util.getTmpFileUri
import com.noljanolja.android.util.showToast
import com.yalantis.ucrop.UCrop

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AvatarInput(
    isShown: Boolean,
    onAvatarInput: (Uri?) -> Unit,
) {
    val context = LocalContext.current
    val statusBarColor = context.findActivity()?.window?.statusBarColor ?: 0
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val toolbarWidgetColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val selectedFile = remember { mutableStateOf<Uri?>(null) }

    val photoEditorLauncher =
        rememberLauncherForActivityResult(object : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {
            override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent {
                val options = UCrop.Options().apply {
                    setStatusBarColor(statusBarColor)
                    setToolbarColor(toolbarColor)
                    setToolbarWidgetColor(toolbarWidgetColor)
                }

                return UCrop.of(input.first, input.second)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(1000, 1000)
                    .withOptions(options)
                    .getIntent(context)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return intent?.let { UCrop.getOutput(intent) }
            }
        }) {
            onAvatarInput(it)
        }
    val photoLibraryLauncher =
        rememberLauncherForActivityResult(object : ActivityResultContracts.GetContent() {
            override fun createIntent(context: Context, input: String): Intent {
                return super.createIntent(context, input).apply {
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                }
            }
        }) {
            it?.let {
                photoEditorLauncher.launch(Pair(it, context.getTmpFileUri("avatar", ".png")))
            }
        }
    val photoCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                selectedFile.value?.let {
                    photoEditorLauncher.launch(Pair(it, context.getTmpFileUri("avatar", ".png")))
                }
            }
        }
    val selectImageFromCamera = {
        selectedFile.value = context.getTmpFileUri("temp_photo", ".png").also {
            photoCameraLauncher.launch(it)
        }
    }
    val cameraPermissionsState = rememberPermissionState(
        Manifest.permission.CAMERA,
        onPermissionResult = {
            when {
                !it -> context.showToast(context.getString(R.string.permission_camera))
                else -> selectImageFromCamera.invoke()
            }
        }
    )

    if (isShown) {
        AlertDialog(
            title = { Text(stringResource(R.string.update_profile_avatar)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AvatarInputRow(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Camera,
                        label = stringResource(R.string.update_profile_avatar_open_camera),
                        onClick = {
                            if (cameraPermissionsState.status != PermissionStatus.Granted) {
                                cameraPermissionsState.launchPermissionRequest()
                            } else {
                                selectImageFromCamera.invoke()
                            }
                        },
                    )
                    AvatarInputRow(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.PhotoLibrary,
                        label = stringResource(R.string.update_profile_avatar_select_photo),
                        onClick = {
                            photoLibraryLauncher.launch("*/*")
                        },
                    )
                }
            },
            dismissButton = {},
            confirmButton = {},
            onDismissRequest = { onAvatarInput(null) },
        )
    }
}

@Composable
private fun AvatarInputRow(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)

            Text(
                label,
                modifier = Modifier.padding(start = 16.dp).fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}