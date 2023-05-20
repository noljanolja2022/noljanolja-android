package com.noljanolja.android.features.home.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.noljanolja.android.R
import com.noljanolja.android.common.mobiledata.data.StickersLoader
import com.noljanolja.android.util.checkIfExits
import com.noljanolja.core.media.domain.model.Sticker
import com.noljanolja.core.media.domain.model.StickerPack
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

private const val tabSizeDp = 40
private const val stickerSizeDp = 90

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StickerTable(
    modifier: Modifier = Modifier,
    stickersLoader: StickersLoader = get(),
    onStickerClicked: (Long, Sticker) -> Unit,
    onShowSticker: (Sticker?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val stickerPacks by stickersLoader.stickerPacks.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        StickerTabs(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            stickerPacks = stickerPacks,
            selectedTab = pagerState.currentPage,
            onTabSelected = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
        )
        StickerList(
            stickerPacks = stickerPacks,
            pagerState = pagerState,
            onStickerClicked = onStickerClicked,
            onShowSticker = onShowSticker,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
    }
}

@Composable
private fun StickerTabs(
    stickerPacks: List<StickerPack>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.padding(horizontal = 8.dp).size(tabSizeDp.dp),
        ) {
            Icon(
                Icons.Outlined.GifBox,
                contentDescription = null,
            )
        }
        LazyRow(
            state = rememberLazyListState(),
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
        ) {
            if (stickerPacks.isNotEmpty()) {
                repeat(stickerPacks.size) { x ->
                    item {
                        StickerTab(
                            stickerPack = stickerPacks[x],
                            isSelected = selectedTab == x,
                            onTabSelected = { onTabSelected(x) },
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = {},
            modifier = Modifier.padding(horizontal = 8.dp).size(tabSizeDp.dp),
        ) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.ic_shop_find_outline),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun StickerTab(
    stickerPack: StickerPack,
    isSelected: Boolean,
    onTabSelected: () -> Unit,
) {
    IconButton(
        onClick = onTabSelected,
        modifier = Modifier
            .background(
                if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ).padding(4.dp)
            .size(tabSizeDp.dp),
    ) {
        val modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp).size(28.dp)
        if (stickerPack.trayImageFile.checkIfExits()) {
            Image(
                painter = rememberAsyncImagePainter(stickerPack.trayImageFile),
                contentDescription = null,
                modifier = modifier
            )
        } else {
            SubcomposeAsyncImage(
                ImageRequest.Builder(context = LocalContext.current).data(stickerPack.getImageUrl())
                    .memoryCacheKey("${stickerPack.id} ${stickerPack.trayImageFile}")
                    .diskCacheKey("${stickerPack.id} ${stickerPack.trayImageFile}").build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StickerList(
    stickerPacks: List<StickerPack>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    stickersLoader: StickersLoader = get(),
    onStickerClicked: (Long, Sticker) -> Unit,
    onShowSticker: (Sticker?) -> Unit,
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val stickerPerRow = screenWidthDp / stickerSizeDp

    HorizontalPager(
        count = stickerPacks.size,
        state = pagerState,
        modifier = modifier
    ) { page ->
        val stickerPack = stickerPacks[page]
        val stickersPerCategory = stickerPack.stickers.sortedBy { it.imageFile }

        if (stickersPerCategory.isNotEmpty()) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
            ) {
                val stickerRows = with(stickersPerCategory.size / stickerPerRow) {
                    if (stickersPerCategory.size % stickerPerRow == 0) this else this + 1
                }
                repeat(stickerRows) { x ->
                    item {
                        StickerRow(
                            totalStickers = stickersPerCategory,
                            stickerRow = x,
                            stickersPerRow = stickerPerRow,
                            onStickerClicked = { onStickerClicked.invoke(stickerPack.id, it) },
                            onShowSticker = onShowSticker,
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (stickerPack.downloading) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                } else {
                    TextButton(onClick = { stickersLoader.downloadStickerPack(stickerPack) }) {
                        Text(
                            text = stringResource(id = R.string.common_download)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerRow(
    totalStickers: List<Sticker>,
    stickerRow: Int,
    stickersPerRow: Int,
    onStickerClicked: (Sticker) -> Unit,
    onShowSticker: (Sticker?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        repeat(stickersPerRow) { y ->
            val sticker = totalStickers.getOrNull(stickerRow * stickersPerRow + y)
            if (sticker != null) {
                var isPressed by remember { mutableStateOf(false) }
                var isLongClick by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = isPressed, key2 = isLongClick, block = {
                    onShowSticker(sticker.takeIf { isPressed && isLongClick })
                    if (!isPressed) isLongClick = false
                })

                Image(
                    painter = rememberAsyncImagePainter(sticker.imageFile),
                    contentDescription = null,
                    modifier = Modifier.size(stickerSizeDp.dp).pointerInput(Unit) {
                        detectTapGestures(onPress = { _ ->
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }, onTap = { onStickerClicked(sticker) }, onLongPress = { isLongClick = true })
                    }
                )
            } else {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(stickerSizeDp.dp),
                    enabled = false
                ) {}
            }
        }
    }
}