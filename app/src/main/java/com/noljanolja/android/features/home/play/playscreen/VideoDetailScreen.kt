package com.noljanolja.android.features.home.play.playscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.BackPressHandler
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.youtube.YoutubeView
import com.noljanolja.android.ui.theme.OrangeMain
import com.noljanolja.core.video.domain.model.Video
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VideoDetailScreen(
    videoId: String,
    viewModel: VideoDetailViewModel = getViewModel { parametersOf(videoId) },
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

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
            modifier = Modifier.fillMaxSize().padding(it)
        ) {
            YoutubeView(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                onReady = { player -> handleEvent(VideoDetailEvent.ReadyVideo(player)) },
                toggleFullScreen = {
                    isFullScreen = it
                }
            )
            uiState.data?.video?.takeIf { !isFullScreen }?.let { video ->
                SizeBox(height = 8.dp)
                VideoInformation(video = video)
                SizeBox(height = 8.dp)
                VideoParameters(video = video)
                Divider(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
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
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
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
    Box(
        modifier = Modifier.weight(1F).height(55.dp).clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background).shadow(elevation = 8.dp)
            .padding(horizontal = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background),
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

@Composable
private fun VideoComments() {
}