package com.noljanolja.android.features.home.chat.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.android.services.PermissionChecker.Companion.IMAGE_PERMISSION
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.composable.ButtonRadius
import com.noljanolja.android.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PhotoTable(
    mediaList: List<Pair<Uri, Long?>>,
    selectedMedia: List<Uri>,
    modifier: Modifier,
    onMediaSelect: (List<Uri>, Boolean?) -> Unit,
    loadMedia: () -> Unit,
    openPhoneSetting: () -> Unit,
    onOpenFullImages: () -> Unit,
) {
    val libraryLauncher =
        rememberLauncherForActivityResult(object : ActivityResultContracts.GetMultipleContents() {
            override fun createIntent(context: Context, input: String): Intent {
                return super.createIntent(context, input).apply {
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                }
            }
        }) {
            if (it.isNotEmpty()) onMediaSelect(it, null)
        }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
    ) {
        BuildInGallery(
            mediaList = mediaList,
            selectedMedia = selectedMedia,
            onMediaSelect = onMediaSelect,
            loadMedia = loadMedia,
            openPhoneSetting = openPhoneSetting
        )
        FloatingActionButton(
            onClick = onOpenFullImages,
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
        ) {
            Icon(
                Icons.Default.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun BuildInGallery(
    mediaList: List<Pair<Uri, Long?>>,
    selectedMedia: List<Uri>,
    onMediaSelect: (List<Uri>, Boolean) -> Unit,
    loadMedia: () -> Unit,
    openPhoneSetting: () -> Unit,
) {
    val context = LocalContext.current
    val permissionChecker = PermissionChecker(context)
    if (!permissionChecker.canReadExternalStorage()) {
        Rationale(
            modifier = Modifier.fillMaxSize(),
            permissions = mapOf(
                IMAGE_PERMISSION to stringResource(R.string.permission_access_storage_description),
            ),
            onSuccess = { loadMedia.invoke() },
            openPhoneSettings = { openPhoneSetting.invoke() }
        )
    } else {
        GridContent(
            mediaList = mediaList,
            selectedMedia = selectedMedia,
            onMediaSelect = onMediaSelect
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridContent(
    mediaList: List<Pair<Uri, Long?>>,
    selectedMedia: List<Uri>,
    onMediaSelect: (List<Uri>, Boolean) -> Unit,
    isShowSendButton: Boolean = false,
    onSendButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(mediaList.size) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val itemMedia = mediaList[it]
                    PhotoItem(
                        uri = itemMedia.first,
                        durationInMil = itemMedia.second,
                        isSelected = selectedMedia.contains(itemMedia.first),
                        indexSelected = selectedMedia.indexOf(itemMedia.first).plus(1),
                        onMediaSelect = onMediaSelect
                    )
                }
            }
        }
        if(isShowSendButton) {
            ButtonRadius(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(48.dp),
                title = stringResource(id = R.string.common_send),
                bgColor = PrimaryGreen,
                onClick = onSendButtonClick
            )
        }
    }
}

@Composable
fun PhotoItem(
    uri: Uri,
    durationInMil: Long?,
    isSelected: Boolean,
    indexSelected: Int,
    onMediaSelect: (List<Uri>, Boolean) -> Unit,
) {
    val context = LocalContext.current
    Box {
        AsyncImage(
            ImageRequest.Builder(context = context)
                .data(uri)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1F)
                .fillMaxSize()
                .clickable {
                    onMediaSelect(listOf(uri), !isSelected)
                },
            contentScale = ContentScale.Crop,
        )
        if (durationInMil != null && durationInMil != 0L) {
            Text(
                text = setDurationDisplay(durationInMil),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(end = 4.dp, bottom = 4.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(.38F))
                    .padding(vertical = 2.dp, horizontal = 6.dp)
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1F)
                    .background(MaterialTheme.colorScheme.onSurface.copy(.38F))
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .width(24.dp)
                    .aspectRatio(1F)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
            ) {
                Text(
                    text = indexSelected.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.87F),
                    modifier = Modifier
                        .align(Alignment.Center),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .width(24.dp)
                    .aspectRatio(1F)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.background.copy(.87F),
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd),
            )
        }
    }
}

private fun setDurationDisplay(durationInMil: Long): String {
    val hour: Long = durationInMil / 3600000L
    val minute: Long = (durationInMil % 3600000L) / 60000L
    val seconds: Long = (durationInMil % 3600000L) % 60000L / 1000L
    var durationDisplay = ""
    if (hour > 0) {
        durationDisplay = "$hour:"
    }
    durationDisplay += String.format("%02d:%02d", minute.toInt(), seconds.toInt())
    return durationDisplay
}