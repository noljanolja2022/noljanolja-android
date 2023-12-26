package com.noljanolja.android.features.home.play.playlist

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import coil.compose.*
import coil.request.*
import com.google.accompanist.pager.*
import com.noljanolja.android.R
import com.noljanolja.android.common.Const.VIDEO_IMAGE_RATIO
import com.noljanolja.android.common.base.*
import com.noljanolja.android.features.home.play.optionsvideo.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.user.domain.model.*
import com.noljanolja.core.video.domain.model.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

@Composable
fun PlayListScreen(
    viewModel: PlayListViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val userStateFlow by viewModel.userStateFlow.collectAsStateWithLifecycle()
    PlayListContent(
        uiState = uiState,
        userStateFlow = userStateFlow,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayListContent(
    uiState: UiState<PlayListUIData>,
    userStateFlow: User,
    handleEvent: (PlayListEvent) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val state = rememberPullRefreshState(uiState.loading, { handleEvent(PlayListEvent.Refresh) })

    var selectOptionsVideo by remember {
        mutableStateOf<Video?>(null)
    }
    ScaffoldWithUiState(uiState = uiState, topBar = {
//        CommonTopAppBar(
//            centeredTitle = true,
//            title = stringResource(id = R.string.video_title),
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            actions = {
//                IconButton(onClick = {
//                    handleEvent(PlayListEvent.Search)
//                }) {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp),
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                }
//            }
//        )
//        CommonAppBarLogoTitle(
//            titleFirstLine = buildAnnotatedString {
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 24.0.sp,
//                        color = textColor()
//                    )
//                ) {
//                    append(stringResource(id = R.string.video_title_part1))
//                }
//                append(" ")
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 24.0.sp,
//                        fontWeight = FontWeight(700),
//                        color = Orange300
//                    )
//                ) {
//                    append(stringResource(id = R.string.video_title_point))
//                }
//            },
//            titleSecondLine = buildAnnotatedString {
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 24.0.sp,
//                        color = textColor()
//                    )
//                ) {
//                    append(stringResource(id = R.string.video_title_by))
//                }
//                append(" ")
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 24.0.sp,
//                        fontWeight = FontWeight(700),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                ) {
//                    append(stringResource(id = R.string.video_title_watching))
//                }
//            },
//            thirdIcon = Icons.Default.Search,
//            thirdIconClickListener = {
//                handleEvent(PlayListEvent.Search)
//            }
//        )

        CommonAppBarSearch(
            modifier = Modifier,
            onSearchFieldClick = {
                handleEvent(PlayListEvent.Search)
            },
            icon = Icons.Filled.Notifications,
            onIconClick = {},
            avatar = userStateFlow.avatar
        )
    }) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state)
        ) {
//            item {
//                HighlightVideos(videos = data.highlightVideos, onClick = {
//                    handleEvent(PlayListEvent.PlayVideo(it.id))
//                })
//                SizeBox(height = 12.dp)
//            }

            if (data.watchingVideos.isNotEmpty()) {
                watchingVideos(
                    videos = data.watchingVideos,
                    onClick = {
                        handleEvent(PlayListEvent.PlayVideo(it.id))
                    },
                    onShowAll = {
                        handleEvent(PlayListEvent.Uncompleted)
                    }
                )
                item {
                    if (data.watchingVideos.isNotEmpty() && data.todayVideos.isNotEmpty()) {
                        Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                    }
                }
            }

            trendingVideos(
                videos = data.todayVideos,
                configuration = configuration,
                onClick = {
                    handleEvent(PlayListEvent.PlayVideo(it.id))
                },
                onMoreVideo = {
                    selectOptionsVideo = it
                }
            )
        }
    }
    selectOptionsVideo?.let {
        OptionVideoBottomBottomSheet(
            visible = true,
            video = it,
            onDismissRequest = {
                selectOptionsVideo = null
            },
            onIgnoreVideoSuccess = {
                handleEvent(PlayListEvent.Refresh)
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HighlightVideos(
    videos: List<Video>,
    onClick: (Video) -> Unit,
) {
    val bannerState = rememberPagerState()

    LaunchedEffect(key1 = bannerState.currentPage) {
        launch {
            delay(3000)
            with(bannerState) {
                val target = if (currentPage < pageCount - 1) currentPage + 1 else 0

                tween<Float>(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
                animateScrollToPage(page = target)
            }
        }
    }

    HorizontalPager(
        count = videos.size,
        state = bannerState,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(VIDEO_IMAGE_RATIO)
    ) { page ->
        val video = videos[page]
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(video.thumbnail).memoryCacheKey("video${video.id}")
                .diskCacheKey("video${video.id}").build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick(video)
                }
        )
    }
    SizeBox(height = 8.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        videos.forEachIndexed { index, _ ->
            val isSelect = index == bannerState.currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(with(MaterialTheme.colorScheme) { if (isSelect) primary else outline })
            )
        }
    }
}

private fun LazyListScope.trendingVideos(
    videos: List<Video>,
    onClick: (Video) -> Unit,
    configuration: Configuration,
    onMoreVideo: (Video) -> Unit,
) {
    if (videos.isEmpty()) return
    item {
        Text(
            stringResource(id = R.string.video_list_today),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        SizeBox(height = 10.dp)
    }
    if (configuration.screenWidthDp < 500) {
        videos.forEach { video ->
            item(key = "trending${video.id}") {
                TrendingVideo(
                    video = video,
                    onClick = { onClick(video) },
                    onMore = onMoreVideo
                )
            }
        }
    } else {
        items((videos.size + 1) / 2) { index ->
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                videos[index * 2].let {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        TrendingVideo(
                            thumbnailModifier = Modifier.padding(start = 16.dp),
                            video = it,
                            onClick = { onClick(it) },
                            onMore = onMoreVideo
                        )
                    }
                }
                videos.getOrNull(index * 2 + 1)?.let {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        TrendingVideo(
                            thumbnailModifier = Modifier.padding(start = 16.dp),
                            video = it,
                            onClick = { onClick(it) },
                            onMore = onMoreVideo
                        )
                    }
                } ?: Box(modifier = Modifier.weight(1f))
                SizeBox(width = 16.dp)
            }
        }
    }
}

private fun LazyListScope.watchingVideos(
    videos: List<Video>,
    onClick: (Video) -> Unit,
    onShowAll: () -> Unit,
) {
    if (videos.isEmpty()) return
    item(key = "watching_title") {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowAll() }
                .padding(horizontal = 16.dp)
        ) {
            Text(
                stringResource(id = R.string.video_list_watching_to_get_point),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        SizeBox(height = 8.dp)
    }
    item {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .wrapContentHeight()
        ) {
            items(items = videos, key = { "watching ${it.id}" }) { video ->
                Column(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .width(142.dp)
                        .clickable {
                            onClick(video)
                        }
                ) {
                    SubcomposeAsyncImage(
                        ImageRequest.Builder(context = LocalContext.current).data(video.thumbnail)
                            .memoryCacheKey("video${video.id}")
                            .diskCacheKey("video${video.id}")
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(142.dp)
                            .aspectRatio(VIDEO_IMAGE_RATIO)

                    )
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline,
                        progress = video.getVideoProgress()
                    )
                    SizeBox(height = 8.dp)
                    Text(
                        text = video.title,
                        style = TextStyle(
                            fontSize = 7.25.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingVideo(
    thumbnailModifier: Modifier = Modifier,
    video: Video,
    onClick: (Video) -> Unit,
    onMore: (Video) -> Unit,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current).data(video.thumbnail)
            .memoryCacheKey("video${video.id}").diskCacheKey("video${video.id}").build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = thumbnailModifier
            .fillMaxWidth()
            .aspectRatio(VIDEO_IMAGE_RATIO)
            .clickable {
                onClick(video)
            }
    )
    if (video.totalPoints > 0) {
        Text(
            text = stringResource(id = R.string.get_point_after_watching, video.totalPoints),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight(700),
                color = Color.Black,
                textAlign = TextAlign.Center,
            ),
            modifier = thumbnailModifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiary)
                .padding(vertical = 3.dp)
        )
    }
    SizeBox(height = 3.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = { onMore(video) }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
    SizeBox(height = 2.dp)
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = video.category.title,
        style = MaterialTheme.typography.bodySmall,
        color = BlueMain
    )
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = video.getShortDescription(context),
        style = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
    )
    SizeBox(height = 10.dp)
}
