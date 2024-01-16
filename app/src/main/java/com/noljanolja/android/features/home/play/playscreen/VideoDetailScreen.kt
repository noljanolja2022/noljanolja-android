package com.noljanolja.android.features.home.play.playscreen

import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.net.*
import android.os.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.features.home.play.playscreen.PlayVideoActivity.Companion.createCustomActions
import com.noljanolja.android.features.home.play.playscreen.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.composable.youtube.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.*
import com.noljanolja.core.user.domain.model.*
import com.noljanolja.core.video.domain.model.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.*
import org.koin.androidx.compose.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VideoDetailScreen(
    isPipMode: Boolean,
    viewModel: VideoDetailViewModel = getViewModel(),
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
        isPipMode = isPipMode,
        handleEvent = viewModel::handleEvent,
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
    isPipMode: Boolean,
    handleEvent: (VideoDetailEvent) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }
    val showOnlyVideo = isFullScreen || isPipMode
    val video = uiState.data?.video

    BackPressHandler() {
        if (isFullScreen) {
            handleEvent(VideoDetailEvent.ToggleFullScreen)
        } else {
            onBack()
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        if (!showOnlyVideo) {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(id = R.string.video_title),
                centeredTitle = true,
                onBack = onBack
            )
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val videoModifier = if (isPipMode) {
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            } else if (showOnlyVideo) {
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            } else {
                val configuration = LocalConfiguration.current
                Modifier.widthIn(max = (configuration.screenHeightDp * 0.6).dp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
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
        }
        SizeBox(width = 15.dp)
        Column {
            Text(
                comment.updatedAt.formatFullTimeNew(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
            SizeBox(height = 2.dp)
            Text(
                text = comment.commenter.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = comment.comment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 10.dp, end = 13.dp)
            )
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