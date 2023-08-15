package com.noljanolja.android.features.home.play.uncompleted

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.Const
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.formatTime
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.video.domain.model.Video
import org.koin.androidx.compose.getViewModel

@Composable
fun UncompletedVideosScreen(
    viewModel: UncompletedVideoViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    UncompletedVideoContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun UncompletedVideoContent(
    uiState: UiState<List<Video>>,
    handleEvent: (UncompletedEvent) -> Unit,
) {
    ScaffoldWithUiState(uiState = uiState, topBar = {
        CommonTopAppBar(
            title = stringResource(id = R.string.uncompleted_video),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            centeredTitle = true,
            onBack = {
                handleEvent(UncompletedEvent.Back)
            }
        )
    }) {
        LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            items(uiState.data.orEmpty()) {
                UncompletedVideoItem(video = it, onClick = {
                    handleEvent(UncompletedEvent.PlayVideo(it.id))
                })
            }
        }
    }
}

@Composable
private fun UncompletedVideoItem(video: Video, onClick: (Video) -> Unit) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current).data(video.thumbnail)
            .memoryCacheKey("video${video.id}").diskCacheKey("video${video.id}").build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(Const.VIDEO_IMAGE_RATIO)
            .clickable {
                onClick(video)
            }
    )
    Text(
        text = stringResource(id = R.string.get_point_after_watching, video.totalPoints),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight(700),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(vertical = 3.dp)
    )
    SizeBox(height = 8.dp)
    Text(
        text = video.title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(id = R.string.video_completed_state, video.getVideoPercentProgress()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            "${video.currentProgressMs.formatTime(context)} / ${video.durationMs.formatTime(context)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.secondaryTextColor()
        )
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(7.dp)
            .clip(RoundedCornerShape(10.dp)),
        color = MaterialTheme.colorScheme.primaryContainer,
        trackColor = MaterialTheme.colorScheme.outline,
        progress = video.getVideoProgress(),
    )
    SizeBox(height = 20.dp)
}