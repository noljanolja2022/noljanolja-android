package com.noljanolja.android.features.home.play.optionsvideo

import android.view.Gravity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.Const
import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Blue00
import com.noljanolja.android.util.getShortDescription
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.showError
import com.noljanolja.android.util.showToast
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.getViewModel

@Composable
fun OptionVideoBottomBottomSheet(
    visible: Boolean = false,
    video: Video,
    onDismissRequest: () -> Unit,
    videoViewModel: OptionsVideoViewModel = getViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(videoViewModel.shareSuccessEvent) {
        videoViewModel.shareSuccessEvent.collectLatest {
            context.showToast(context.getString(R.string.common_share_success))
            onDismissRequest()
        }
    }
    LaunchedEffect(videoViewModel.errorFlow) {
        videoViewModel.errorFlow.collectLatest {
            context.showError(it)
        }
    }
    val contacts by videoViewModel.contactsFlow.collectAsStateWithLifecycle()
    var selectContact by remember {
        mutableStateOf<ShareContact?>(null)
    }
    var isSelectConversation by remember {
        mutableStateOf(false)
    }
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
            dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
            dialogWindowProvider.window.setBackgroundDrawableResource(R.color.black30)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismissRequest() },
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = false) { }
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .height(IntrinsicSize.Min)
                            .background(MaterialTheme.colorScheme.background)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val (x, y) = dragAmount
                                    if (y > 50) onDismissRequest()

                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .padding(top = 10.dp, bottom = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when {
                            selectContact != null -> {
                                ShareVideoContent(
                                    shareContact = selectContact!!,
                                    video = video,
                                    onShareVideo = { video, contact ->
                                        videoViewModel.handleEvent(
                                            OptionsVideoEvent.ShareVideo(
                                                video,
                                                contact
                                            )
                                        )
                                    }
                                )
                            }

                            isSelectConversation -> {
                                SelectConversation(
                                    contacts = contacts,
                                    onSelectContact = {
                                        selectContact = it
                                    }
                                )
                            }

                            else -> {
                                OptionsContent(onShare = {
                                    isSelectConversation = true
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareVideoContent(
    shareContact: ShareContact,
    video: Video,
    onShareVideo: (Video, ShareContact) -> Unit,
) {
    val context = LocalContext.current
    SizeBox(height = 24.dp)
    Text(
        "Send to",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 10.dp)
    OvalAvatar(avatar = shareContact.avatar)
    SizeBox(height = 10.dp)
    Text(
        shareContact.title,
        maxLines = 2,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 10.dp)
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current)
            .data(video.thumbnail)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(Const.VIDEO_IMAGE_RATIO)
    )
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        SizeBox(height = 2.dp)
        Text(
            text = video.category.title,
            style = MaterialTheme.typography.labelMedium,
            color = Blue00
        )
        SizeBox(height = 2.dp)
        Text(
            text = video.getShortDescription(context),
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.secondaryTextColor()
            ),
        )
        SizeBox(height = 44.dp)
        PrimaryButton(text = stringResource(id = R.string.common_share).uppercase()) {
            onShareVideo(video, shareContact)
        }

        SizeBox(height = 24.dp)
    }
}

@Composable
private fun SelectConversation(
    contacts: List<ShareContact>,
    onSelectContact: (ShareContact) -> Unit,
) {
    SizeBox(height = 10.dp)
    Text(
        stringResource(id = R.string.common_share),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 20.dp)
    LazyRow(
        modifier = Modifier
            .height(90.dp)
            .padding(horizontal = 12.dp)
    ) {
        items(contacts.size) {
            val contact = contacts[it]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(56.dp)
                    .clickable {
                        onSelectContact(contact)
                    }
            ) {
                OvalAvatar(avatar = contact.avatar, size = 32.dp)
                SizeBox(height = 10.dp)
                Text(
                    contact.title,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    Divider()
    SizeBox(height = 20.dp)
    LazyRow(
        modifier = Modifier
            .padding(12.dp)
            .height(48.dp)
    ) {
        item {
            Image(
                ImageVector.vectorResource(R.drawable.facebook),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.twiter),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.titkok),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.telegram),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.twitch),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            SizeBox(width = 30.dp)
        }
    }
    SizeBox(height = 30.dp)
}

@Composable
private fun OptionsContent(
    onShare: () -> Unit,
) {
    OptionVideoRow(
        icon = ImageVector.vectorResource(id = R.drawable.ic_share),
        text = stringResource(R.string.common_share),
        onClick = onShare
    )
    OptionVideoRow(
        icon = Icons.Default.Link,
        text = stringResource(R.string.common_copy)
    ) {}
    OptionVideoRow(
        icon = Icons.Default.Block,
        text = stringResource(R.string.ignore_video)
    ) {}
}

@Composable
private fun OptionVideoRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        SizeBox(width = 5.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}