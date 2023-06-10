package com.noljanolja.android.features.qrcode

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.noljanolja.android.R
import com.noljanolja.android.common.error.QrNotValidFailure
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.util.getErrorDescription
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanQrCodeScreen(
    viewModel: ScanQrCodeViewModel = getViewModel(),
) {
    var error by remember { mutableStateOf<Throwable?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOfNotNull(
            Manifest.permission.CAMERA,
        ),
        onPermissionsResult = {
            if (it[Manifest.permission.CAMERA] == true) {
                hasCamPermission = true
            }
        }
    )
    val photoLibraryLauncher =
        rememberLauncherForActivityResult(object : ActivityResultContracts.GetContent() {
            override fun createIntent(context: Context, input: String): Intent {
                return super.createIntent(context, input).apply {
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                }
            }
        }) {
            it?.let {
                val result = parseImage(it, context)
                if (result != null) {
                    viewModel.handleEvent(ScanQrCodeEvent.ParseQRSuccess(result))
                } else {
                    error = QrNotValidFailure
                }
            }
        }

    LaunchedEffect(key1 = true) {
        if (!multiplePermissionsState.allPermissionsGranted) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
    Scaffold(
        topBar = {
            CommonTopAppBar(
                onBack = {
                    viewModel.handleEvent(ScanQrCodeEvent.Back)
                },
                title = stringResource(id = R.string.scan_qr_code_title),
                centeredTitle = true,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box {
                if (hasCamPermission) {
                    AndroidView(
                        factory = { context ->
                            val previewView = PreviewView(context)
                            val preview = Preview.Builder().build()
                            val selector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(
                                    Size(
                                        previewView.width,
                                        previewView.height
                                    )
                                )
                                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                QrCodeAnalyzer { result ->
                                    viewModel.handleEvent(ScanQrCodeEvent.ParseQRSuccess(result))
                                }
                            )
                            try {
                                cameraProviderFuture.get().bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            previewView
                        },
                        modifier = Modifier.matchParentSize()
                    )
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_qr_focus),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Expanded()
            PrimaryButton(
                text = stringResource(id = R.string.chat_action_album),
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                photoLibraryLauncher.launch("*/*")
            }
        }
        error?.let {
            ErrorDialog(
                showError = true,
                title = stringResource(id = R.string.common_error_title),
                description = context.getErrorDescription(it)
            ) {
                error = null
            }
        }
    }
}

private fun parseImage(imageUri: Uri, context: Context): String? = try {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val source = RGBLuminanceSource(
        bitmap.width,
        bitmap.height,
        IntArray(bitmap.width * bitmap.height).apply {
            bitmap.getPixels(this, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        }
    )
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    MultiFormatReader().decode(binaryBitmap).text
} catch (e: Throwable) {
    null
}