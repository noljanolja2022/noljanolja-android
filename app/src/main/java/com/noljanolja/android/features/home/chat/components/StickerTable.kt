//package com.noljanolja.android.features.home.chat.components
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Divider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import coil.compose.rememberAsyncImagePainter
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.PagerState
//import com.google.accompanist.pager.rememberPagerState
//import kotlinx.coroutines.launch
//
//
//private const val tabSizeDp = 40
//private const val stickerSizeDp = 90
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun StickerTable(
//    modifier: Modifier = Modifier,
//    stickersLoader: StickersLoader = get(),
//    onStickerClicked: (Sticker) -> Unit,
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val pagerState = rememberPagerState()
//    val stickerPacks = remember { stickersLoader.stickerPacks }
//
//    Column(modifier = modifier) {
//        StickerList(
//            stickerPacks = stickerPacks,
//            pagerState = pagerState,
//            onStickerClicked = onStickerClicked,
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//        )
//        StickerTabs(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            stickerPacks = stickerPacks,
//            selectedTab = pagerState.currentPage,
//            onTabSelected = {
//                coroutineScope.launch {
//                    pagerState.animateScrollToPage(it)
//                }
//            }
//        )
//    }
//}
//
//@Composable
//private fun StickerTabs(
//    stickerPacks: List<StickerPack>,
//    selectedTab: Int,
//    onTabSelected: (Int) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        LazyRow(
//            state = rememberLazyListState(),
//            modifier = Modifier.weight(1f),
//            verticalAlignment = Alignment.Top,
//            horizontalArrangement = Arrangement.Start,
//        ) {
//            if (stickerPacks.isNotEmpty()) {
//                repeat(stickerPacks.size) { x ->
//                    item {
//                        StickerTab(
//                            stickerPack = stickerPacks[x],
//                            isSelected = selectedTab == x,
//                            onTabSelected = { onTabSelected(x) },
//                        )
//                    }
//                }
//            }
//        }
//        Divider(
//            modifier = Modifier
//                .width(1.dp)
//                .height(24.dp)
//                .background(MaterialTheme.colorScheme.onSurface.copy(.12f))
//        )
//        IconButton(
//            onClick = {},
//            modifier = Modifier
//                .padding(horizontal = 8.dp)
//                .size(tabSizeDp.dp),
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.ic_store_line),
//                contentDescription = null,
//            )
//        }
//    }
//}
//
//@Composable
//private fun StickerTab(
//    stickerPack: StickerPack,
//    isSelected: Boolean,
//    onTabSelected: () -> Unit,
//) {
//    Box(
//        modifier = Modifier
//            .padding(horizontal = 2.dp)
//            .size(tabSizeDp.dp),
//        contentAlignment = Alignment.BottomCenter,
//    ) {
//        if (isSelected) {
//            Box(
//                modifier = Modifier
//                    .padding(vertical = 6.dp)
//                    .fillMaxSize()
//                    .background(
//                        MaterialTheme.colorScheme.secondaryContainer,
//                        shape = RoundedCornerShape(18.dp)
//                    )
//            )
//        }
//        IconButton(
//            onClick = onTabSelected,
//            modifier = Modifier.size(tabSizeDp.dp),
//        ) {
//            Image(
//                painter = rememberAsyncImagePainter(stickerPack.trayImageFile),
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(vertical = 6.dp, horizontal = 8.dp)
//                    .size(28.dp)
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//private fun StickerList(
//    stickerPacks: List<StickerPack>,
//    pagerState: PagerState,
//    modifier: Modifier = Modifier,
//    onStickerClicked: (Sticker) -> Unit,
//) {
//    val screenWidthDp = LocalConfiguration.current.screenWidthDp
//    val stickerPerRow = screenWidthDp / stickerSizeDp
//
//    HorizontalPager(
//        count = stickerPacks.size,
//        state = pagerState,
//        modifier = modifier
//    ) { page ->
//        val stickersPerCategory = stickerPacks[page].stickers.sortedBy { it.imageFile }
//
//        LazyColumn(
//            state = rememberLazyListState(),
//            modifier = modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Top,
//        ) {
//            if (stickersPerCategory.isNotEmpty()) {
//                val stickerRows = with(stickersPerCategory.size / stickerPerRow) {
//                    if (stickersPerCategory.size % stickerPerRow == 0) this else this + 1
//                }
//                repeat(stickerRows) { x ->
//                    item {
//                        StickerRow(
//                            totalStickers = stickersPerCategory,
//                            stickerRow = x,
//                            stickersPerRow = stickerPerRow,
//                            onStickerClicked = onStickerClicked,
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun StickerRow(
//    totalStickers: List<Sticker>,
//    stickerRow: Int,
//    stickersPerRow: Int,
//    onStickerClicked: (Sticker) -> Unit,
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//    ) {
//        repeat(stickersPerRow) { y ->
//            val sticker = totalStickers.getOrNull(stickerRow * stickersPerRow + y)
//            if (sticker != null) {
//                IconButton(
//                    onClick = { onStickerClicked(sticker) },
//                    modifier = Modifier.size(stickerSizeDp.dp),
//                ) {
//                    Image(
//                        painter = rememberAsyncImagePainter(sticker.imageFile),
//                        contentDescription = null
//                    )
//                }
//            } else {
//                IconButton(
//                    onClick = {},
//                    modifier = Modifier.size(stickerSizeDp.dp),
//                    enabled = false
//                ) {}
//            }
//        }
//    }
//}