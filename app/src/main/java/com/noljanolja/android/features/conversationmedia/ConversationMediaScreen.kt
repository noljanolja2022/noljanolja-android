package com.noljanolja.android.features.conversationmedia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.chat.components.AsyncImageState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.shopBackground
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.openUrl
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.conversation.domain.model.ConversationMedia
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ConversationMediaScreen(
    conversationId: Long,
    viewModel: ConversationMediaViewModel = getViewModel { parametersOf(conversationId) },
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ConversationMediaContent(
        conversationId = conversationId,
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun ConversationMediaContent(
    conversationId: Long,
    uiState: UiState<MutableMap<ConversationMedia.AttachmentType, List<ConversationMedia>>>,
    handleEvent: (ConversationMediaEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                onBack = { handleEvent(ConversationMediaEvent.Back) },
                centeredTitle = true,
                title = "Images/Files",
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        }
    ) {
        var tabIndex by remember { mutableStateOf(0) }

        val tabs = listOf("Images, Videos", "Files", "Links")

        Column(modifier = Modifier.fillMaxWidth()) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 20.dp),
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }
            when (tabIndex) {
                0 -> ImagesContent(
                    uiState.data?.get(ConversationMedia.AttachmentType.PHOTO).orEmpty(),
                    conversationId = conversationId,
                    onClick = {
                        handleEvent(ConversationMediaEvent.ViewImages(it.getCacheKey()))
                    },
                    onLoadMore = {
                        handleEvent(ConversationMediaEvent.LoadMoreImage)
                    }
                )

                1 -> FilesContent(
                    uiState.data?.get(ConversationMedia.AttachmentType.FILE).orEmpty(),
                    onLoadMore = {
                        handleEvent(ConversationMediaEvent.LoadMoreFile)
                    }
                )

                2 -> LinksContent(
                    uiState.data?.get(ConversationMedia.AttachmentType.LINK).orEmpty(),
                    onLoadMore = {
                        handleEvent(ConversationMediaEvent.LoadMoreLink)
                    }
                )
            }
        }
    }
}

@Composable
private fun ImagesContent(
    photos: List<ConversationMedia>,
    conversationId: Long,
    onClick: (ConversationMedia) -> Unit,
    onLoadMore: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val columnRow = configuration.screenWidthDp / 120
    val scrollState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnRow),
        contentPadding = PaddingValues(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        state = scrollState
    ) {
        items(photos) {
            ImageItem(
                photo = it,
                conversationId = conversationId,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        onClick(it)
                    }
            )
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = onLoadMore)
}

@Composable
fun ImageItem(
    photo: ConversationMedia,
    conversationId: Long,
    modifier: Modifier = Modifier,
) {
    val uri = photo.getAttachmentUrl(conversationId)
    Box(
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(uri)
                .memoryCacheKey(photo.getCacheKey())
                .diskCacheKey(photo.getCacheKey())
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        ) {
            AsyncImageState(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F),
                contentScale = contentScale,
            )
        }
    }
}

@Composable
private fun FilesContent(
    files: List<ConversationMedia>,
    onLoadMore: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        state = scrollState
    ) {
        items(files) {
            FileItem(it)
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = onLoadMore)
}

@Composable
private fun FileItem(file: ConversationMedia) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.shopBackground())
            .padding(vertical = 17.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.InsertDriveFile, contentDescription = null)
        SizeBox(width = 20.dp)
        Column {
            Text(
                file.name,
                style = MaterialTheme.typography.bodyMedium.withBold(),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Friday, 15 December, 2022",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.secondaryTextColor()
            )
        }
    }
    SizeBox(height = 10.dp)
}

@Composable
private fun LinksContent(
    links: List<ConversationMedia>,
    onLoadMore: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        state = scrollState
    ) {
        items(links) {
            LinkItem(link = it)
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = onLoadMore)
}

@Composable
private fun LinkItem(link: ConversationMedia) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                context.openUrl(link.originalName)
            }
    ) {
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(link.previewImage)
                .memoryCacheKey(link.originalName)
                .diskCacheKey(link.originalName)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(69.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            if (painter.state is AsyncImagePainter.State.Error) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Link,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
        SizeBox(width = 12.dp)
        Column(modifier = Modifier.height(69.dp), verticalArrangement = Arrangement.Center) {
            Text(
                link.originalName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.withBold()
            )
            SizeBox(2.dp)
            Text(
                text = "Friday, 17 December, 2022",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.secondaryTextColor()
            )
        }
    }
    SizeBox(height = 10.dp)
}