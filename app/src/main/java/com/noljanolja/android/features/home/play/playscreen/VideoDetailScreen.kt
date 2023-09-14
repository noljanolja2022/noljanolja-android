package com.noljanolja.android.features.home.play.playscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.MainActivity
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.play.playscreen.PlayVideoActivity.Companion.createCustomActions
import com.noljanolja.android.features.home.play.playscreen.composable.CommentInput
import com.noljanolja.android.ui.composable.BackPressHandler
import com.noljanolja.android.ui.composable.BoxWithBottomElevation
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.VerticalDivider
import com.noljanolja.android.ui.composable.WarningDialog
import com.noljanolja.android.ui.composable.youtube.YoutubeView
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.NeutralGrey
import com.noljanolja.android.ui.theme.NeutralLightGrey
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatFullTime
import com.noljanolja.android.util.showToast
import com.noljanolja.core.Failure
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Commenter
import com.noljanolja.core.video.domain.model.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VideoDetailScreen(
    isBottomPlayMode: Boolean,
    isPipMode: Boolean,
    viewModel: VideoDetailViewModel = getViewModel(),
    onToggleBottomPlay: (Boolean) -> Unit,
    onCloseVideo: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showCreateYoutubeDirectionDialog by remember { mutableStateOf(false) }
    val openUrl = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }
    val playerState by viewModel.playerStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.errorFlow, block = {
        viewModel.errorFlow.collect {
            when (it) {
                is Failure.NotHasYoutubeChannel -> {
                    showCreateYoutubeDirectionDialog = true
                }

                else -> context.showToast(it.toString())
            }
        }
    })

    VideoDetailContent(
        uiState = uiState,
        isBottomPlayMode = isBottomPlayMode,
        isPipMode = isPipMode,
        playerState = playerState,
        handleEvent = viewModel::handleEvent,
        onToggleBottomPlay = onToggleBottomPlay,
        onCloseVideo = onCloseVideo,
        onBack = onBack,
    )
    WarningDialog(
        title = stringResource(id = R.string.video_detail_need_youtube_account),
        content = stringResource(id = R.string.video_detail_need_youtube_account_description),
        dismissText = stringResource(R.string.common_cancel),
        confirmText = stringResource(R.string.common_confirm),
        isWarning = showCreateYoutubeDirectionDialog,
        onDismiss = {
            showCreateYoutubeDirectionDialog = false
        },
        onConfirm = {
            showCreateYoutubeDirectionDialog = false
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(helpYoutubeUri)
            )
            openUrl.launch(intent)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoDetailContent(
    uiState: UiState<VideoDetailUiData>,
    isBottomPlayMode: Boolean,
    isPipMode: Boolean,
    playerState: PlayerConstants.PlayerState,
    handleEvent: (VideoDetailEvent) -> Unit,
    onToggleBottomPlay: (Boolean) -> Unit,
    onCloseVideo: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }
    val showOnlyVideo = isFullScreen || isBottomPlayMode || isPipMode
    val video = uiState.data?.video

    BackPressHandler() {
        if (isFullScreen) {
            handleEvent(VideoDetailEvent.ToggleFullScreen)
        } else if (isBottomPlayMode) {
            (context as MainActivity).enterPip()
        } else {
            onToggleBottomPlay(true)
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        if (!showOnlyVideo) {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(id = R.string.video_title),
                onBack = onBack
            )
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (!showOnlyVideo && video != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.video_earn_point, video.earnedPoints),
                        style = MaterialTheme.typography.bodyMedium.withBold(),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_video_point),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 1.dp)
                            .size(18.dp)
                    )
                }
            }
            val videoModifier = if (isPipMode) {
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            } else if (isBottomPlayMode) {
                Modifier
                    .height(60.dp)
                    .width(106.dp)
            } else if (showOnlyVideo) {
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            } else {
                val configuration = LocalConfiguration.current
                Modifier.widthIn(max = (configuration.screenHeightDp * 0.6).dp)
            }
            val rowModifier = if (isBottomPlayMode) Modifier.height(60.dp) else Modifier
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = rowModifier
                        .background(NeutralDarkGrey)
                        .clickable {
                            if (isBottomPlayMode) {
                                onToggleBottomPlay(false)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        YoutubeView(
                            modifier = videoModifier,
                            onReady = { player -> handleEvent(VideoDetailEvent.ReadyVideo(player)) },
                            toggleFullScreen = {
                                isFullScreen = it
                            }
                        )
                    }
                    if (isBottomPlayMode && !isPipMode) {
                        SizeBox(12.dp)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                        ) {
                            Text(
                                video?.title.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = NeutralLightGrey
                            )
                            Text(
                                video?.category?.title.orEmpty(),
                                color = NeutralGrey
                            )
                        }
                        Icon(
                            if (playerState == PlayerConstants.PlayerState.PLAYING) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    handleEvent(VideoDetailEvent.TogglePlayPause)
                                }
                                .padding(8.dp),
                            tint = NeutralLightGrey
                        )
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onCloseVideo()
                                }
                                .padding(8.dp),
                            tint = NeutralLightGrey
                        )
                    }
                }
            }

            uiState.data?.video?.takeIf { !showOnlyVideo }?.let { video ->
                LazyColumn(modifier = Modifier.weight(1F)) {
                    item {
                        SizeBox(height = 8.dp)
                        VideoInformation(video = video)
                        SizeBox(height = 8.dp)
                        VideoParameters(video = video)
                        Divider(
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    videoComments(video)
                }
                uiState.data.user?.let { user ->
                    CommentInput(me = user, onSend = { text, token ->
                        handleEvent(VideoDetailEvent.Comment(text, token))
                    }, onError = {
                        handleEvent(VideoDetailEvent.SendError(it))
                    })
                }
            }
        }
    }
}

@Composable
private fun VideoInformation(video: Video) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
    }
    Text(
        text = video.category.title,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.tertiary
    )
    Text(
        text = video.channel.title,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun VideoParameters(video: Video) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        VideoParameter(
            title = stringResource(id = R.string.video_detail_views),
            value = video.viewCount.toString()
        )
        SizeBox(width = 10.dp)
        VideoParameter(
            title = stringResource(id = R.string.video_detail_comment),
            value = video.commentCount.toString()
        )
        SizeBox(width = 10.dp)
        VideoParameter(
            title = stringResource(id = R.string.video_detail_reward),
            value = stringResource(id = R.string.video_detail_reward_point, video.totalPoints),
            valueColor = Orange300
        )
    }
}

@Composable
private fun RowScope.VideoParameter(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    BoxWithBottomElevation(
        modifier = Modifier
            .weight(1F)
            .height(55.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = valueColor
            )
        }
    }
}

private fun LazyListScope.videoComments(video: Video) {
    val modifier = Modifier.padding(horizontal = 16.dp)
    item {
        Text(
            stringResource(id = R.string.video_detail_comment),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
        SizeBox(height = 8.dp)
    }
    item {
        Row(modifier = modifier) {
            VideoCommentSortType.values().forEachIndexed { _, commentSortType ->
                CommentSortItem(
                    type = commentSortType,
                    isSelect = commentSortType == VideoCommentSortType.Popular
                )
                SizeBox(width = 10.dp)
            }
        }
    }
    items(video.comments) {
        CommentRow(modifier = modifier, comment = it)
    }
}

@Composable
private fun CommentSortItem(
    type: VideoCommentSortType,
    isSelect: Boolean,
) {
    val containerColor: Color
    val contentColor: Color
    with(MaterialTheme.colorScheme) {
        if (isSelect) {
            containerColor = outline
            contentColor = onPrimary
        } else {
            containerColor = surface
            contentColor = outline
        }
    }
    Text(
        text = stringResource(id = type.id),
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(containerColor)
            .padding(vertical = 5.dp, horizontal = 10.dp),
        color = contentColor,
    )
}

@Composable
private fun CommentRow(
    modifier: Modifier = Modifier,
    comment: Comment,
) {
    Row(
        modifier = modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommenterAvatar(commenter = comment.commenter)
            SizeBox(height = 12.dp)
            VerticalDivider(modifier = Modifier.fillMaxHeight())
        }
        SizeBox(width = 15.dp)
        Column {
            Text(
                comment.updatedAt.formatFullTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
            SizeBox(height = 2.dp)
            Text(
                text = comment.commenter.name,
                style = MaterialTheme.typography.titleMedium
            )
            SizeBox(height = 8.dp)
            BoxWithBottomElevation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = comment.comment,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp, horizontal = 13.dp)
                )
            }
            SizeBox(height = 8.dp)
            Divider()
            SizeBox(height = 10.dp)
        }
    }
}

@Composable
private fun CommenterAvatar(commenter: Commenter) {
    CircleAvatar(user = User(avatar = commenter.avatar), size = 25.dp)
}

private fun enterPictureInPicture(context: Context, playerState: PlayerConstants.PlayerState) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val supportsPIP: Boolean =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        if (supportsPIP) {
            val params = PictureInPictureParams.Builder()
                .setActions(listOf(createCustomActions(context, playerState)))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                params.setSeamlessResizeEnabled(true)
            }
            (context as Activity).enterPictureInPictureMode(params.build())
        }
    } else {
        AlertDialog.Builder(context)
            .setTitle("Can't enter picture in picture mode")
            .setMessage("In order to enter picture in picture mode you need a Android version >= 8")
            .show()
    }
}

private const val helpYoutubeUri =
    "https://support.google.com/youtube/answer/1646861?topic=3024170&hl=en"