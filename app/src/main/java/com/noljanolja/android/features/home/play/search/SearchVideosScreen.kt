package com.noljanolja.android.features.home.play.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.play.optionsvideo.OptionVideoBottomBottomSheet
import com.noljanolja.android.features.home.play.playlist.TrendingVideo
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralGrey
import com.noljanolja.core.video.domain.model.Video
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchVideosScreen(viewModel: SearchVideosViewModel = getViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val searchKeys by viewModel.searchKeys.collectAsStateWithLifecycle()
    SearchVideosContent(
        uiState = uiState,
        searchKeys = searchKeys,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun SearchVideosContent(
    uiState: UiState<SearchVideosUiData>,
    searchKeys: List<String>,
    handleEvent: (SearchVideosEvent) -> Unit,
) {
    var isSearchFocus by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    var searchText by remember {
        mutableStateOf("")
    }
    var selectOptionsVideo by remember {
        mutableStateOf<Video?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SearchVideoHeader(
            searchText = searchText,
            onSearchChange = {
                searchText = it
            },
            onFocusChange = {
                isSearchFocus = it
            },
            onBack = {
                handleEvent(SearchVideosEvent.Back)
            },
            onSubmit = {
                handleEvent(SearchVideosEvent.Search(searchText))
            }
        )
        if (isSearchFocus) {
            SearchVideosHistory(
                searchKeys = searchKeys,
                onClear = {
                    handleEvent(SearchVideosEvent.Clear(it))
                },
                onClearAll = {
                    handleEvent(SearchVideosEvent.ClearAll)
                },
                onSearch = {
                    handleEvent(SearchVideosEvent.Search(it))
                    searchText = it
                    focusManager.clearFocus()
                }
            )
        } else {
            val videos = uiState.data?.videos.orEmpty()
            SearchVideosResult(
                videos = videos,
                onMoreVideo = {
                    selectOptionsVideo = it
                },
                onClick = {
                    handleEvent(SearchVideosEvent.PlayVideo(it.id))
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
                handleEvent(SearchVideosEvent.Search(searchText))
            }
        )
    }
}

@Composable
private fun SearchVideoHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable {
                    onBack.invoke()
                }
            )
            SizeBox(width = 15.dp)
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                searchText = searchText,
                hint = stringResource(id = R.string.search_videos),
                onSearch = onSearchChange,
                background = MaterialTheme.colorScheme.background,
                onFocusChange = {
                    onFocusChange.invoke(it.isFocused)
                },
                onSearchButton = {
                    if (searchText.isNotBlank()) {
                        onSubmit()
                        focusManager.clearFocus()
                    }
                },
                focusRequester = focusRequester
            )
        }
    }
}

@Composable
private fun SearchVideosHistory(
    searchKeys: List<String>,
    onSearch: (String) -> Unit,
    onClear: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    if (searchKeys.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SizeBox(height = 10.dp)
        Text(
            text = stringResource(id = R.string.shop_clear_all),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClearAll.invoke() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        searchKeys.forEach {
            SizeBox(height = 10.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onSearch(it)
                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_schedule),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = NeutralGrey
                )
                SizeBox(width = 10.dp)
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Expanded()
                Icon(
                    Icons.Rounded.Cancel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClear.invoke(it) },
                    tint = NeutralGrey
                )
            }
        }
    }
}

@Composable
private fun SearchVideosResult(
    videos: List<Video>,
    onMoreVideo: (Video) -> Unit,
    onClick: (Video) -> Unit,
) {
    val configuration = LocalConfiguration.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
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
                                modifier = Modifier.padding(start = 16.dp),
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
                                modifier = Modifier.padding(start = 16.dp),
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
}