package com.noljanolja.android.features.home.play.playscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launchInMainIO
import com.noljanolja.android.common.sharedpreference.SharedPreferenceHelper
import com.noljanolja.android.features.home.play.playscreen.composable.CommentInput
import com.noljanolja.android.ui.composable.BackPressHandler
import com.noljanolja.android.ui.composable.BoxWithBottomElevation
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.VerticalDivider
import com.noljanolja.android.ui.composable.WarningDialog
import com.noljanolja.android.ui.composable.youtube.YoutubeView
import com.noljanolja.android.ui.theme.OrangeMain
import com.noljanolja.android.util.findActivity
import com.noljanolja.android.util.showToast
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Commenter
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VideoDetailScreen(
    videoId: String,
    viewModel: VideoDetailViewModel = getViewModel { parametersOf(videoId) },
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    HandleComments(viewModel = viewModel)

    LaunchedEffect(key1 = viewModel.errorFlow, block = {
        viewModel.errorFlow.collect {
            context.showToast(it.message)
        }
    })
    VideoDetailContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoDetailContent(
    uiState: UiState<VideoDetailUiData>,
    handleEvent: (VideoDetailEvent) -> Unit,
) {
    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    BackPressHandler(isFullScreen) {
        handleEvent(VideoDetailEvent.ToggleFullScreen)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        if (!isFullScreen) {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = stringResource(id = R.string.video_title),
                onBack = {
                    handleEvent(VideoDetailEvent.Back)
                }
            )
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            YoutubeView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onReady = { player -> handleEvent(VideoDetailEvent.ReadyVideo(player)) },
                toggleFullScreen = {
                    isFullScreen = it
                }
            )
            uiState.data?.video?.takeIf { !isFullScreen }?.let { video ->
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
                    CommentInput(me = user, onSend = { text ->
                        handleEvent(VideoDetailEvent.Comment(text))
                    })
                }
            }
        }
    }
}

@Composable
private fun VideoInformation(video: Video) {
    Text(
        text = video.title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
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
            value = video.likeCount.toString()
        )
        SizeBox(width = 10.dp)
        VideoParameter(
            title = stringResource(id = R.string.video_detail_comment),
            value = video.commentCount.toString()
        )
        SizeBox(width = 10.dp)
        VideoParameter(
            title = stringResource(id = R.string.video_detail_reward),
            value = "90 Points",
            valueColor = OrangeMain
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
            modifier = Modifier
                .fillMaxSize(),
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
            "Comments",
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
                "2022.05.12  11:13",
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

@Composable
private fun HandleComments(
    viewModel: VideoDetailViewModel,
) {
    var showRequireGoogle by remember { mutableStateOf(false) }

    val sharedPreferenceHelper: SharedPreferenceHelper = get()
    val context = LocalContext.current
    val activity = context.findActivity()!!
    var currentComment by remember {
        mutableStateOf("")
    }
    val scope = Scope("https://www.googleapis.com/auth/youtube.force-ssl")
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(stringResource(id = R.string.web_client_id))
        .requestScopes(scope)
        .build()

    val googleSignInClient = GoogleSignIn.getClient(LocalContext.current, gso)
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            try {
                launchInMainIO {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getResult(ApiException::class.java)
                    if (!GoogleSignIn.hasPermissions(
                            GoogleSignIn.getLastSignedInAccount(activity),
                            scope
                        )
                    ) {
                        GoogleSignIn.requestPermissions(
                            activity,
                            GOOGLE_REQUEST_PERMISSION_CODE,
                            GoogleSignIn.getLastSignedInAccount(activity),
                            scope
                        )
                    }

                    val token = GoogleAuthUtil.getToken(
                        context,
                        account.account!!,
                        "oauth2:https://www.googleapis.com/auth/youtube.force-ssl"
                    )
                    sharedPreferenceHelper.youtubeToken = token
                    viewModel.handleEvent(VideoDetailEvent.Comment(currentComment, token))
                }
            } catch (e: Throwable) {
                context.showToast(e.message)
            }
        }
    )
    LaunchedEffect(key1 = viewModel.eventForceLoginGoogle) {
        viewModel.eventForceLoginGoogle.collect { comment ->
            currentComment = comment
            showRequireGoogle = true
        }
    }

    WarningDialog(
        title = stringResource(id = R.string.common_warning),
        content = stringResource(R.string.video_detail_require_google_description),
        dismissText = stringResource(R.string.common_cancel),
        confirmText = stringResource(R.string.common_confirm),
        isWarning = showRequireGoogle,
        onDismiss = {
            showRequireGoogle = false
        },
        onConfirm = {
            showRequireGoogle = false
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    )
}

const val GOOGLE_REQUEST_PERMISSION_CODE = 1000