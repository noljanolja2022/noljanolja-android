// package com.noljanolja.android.features.home.chat.components
//
// import androidx.compose.animation.*
// import androidx.compose.foundation.*
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.lazy.*
// import androidx.compose.material3.FloatingActionButton
// import androidx.compose.material3.Icon
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.graphicsLayer
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.res.stringResource
// import androidx.compose.ui.unit.dp
// import coil.compose.AsyncImage
// import com.google.accompanist.pager.ExperimentalPagerApi
//
// @OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
// @Composable
// fun GifTable(
//    modifier: Modifier,
//    onGifClick: (Gif) -> Unit,
//    onLoadMoreGifs: (String) -> Unit,
//    onExpandedGifList: (Boolean) -> Unit,
//    onSearch: (String) -> Unit,
//    gifs: List<Gif>,
//    isLoading: Boolean,
//    isDisplaySearchBar: Boolean,
//    currentFraction: Float,
// ) {
//    var searchText by remember { mutableStateOf("") }
//
//    Box(
//        modifier = modifier,
//        contentAlignment = Alignment.TopStart,
//    ) {
//        Column(modifier = modifier) {
//            // Basically the height of search bar is still wrap content, 64.dp is the height container
//            // We need a specific number to animate, we choose 64 to make sure search bar is not overflow
//            SearchBar(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height((64 * currentFraction).dp)
//                    .wrapContentHeight()
//                    .padding(top = 8.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
//                searchText = searchText,
//                hint = stringResource(R.string.chat_gif_search_hint),
//                onSearch = {
//                    searchText = it
//                    onSearch(it)
//                },
//            )
//
//            when {
//                isLoading -> LoadingPage()
//                gifs.isNullOrEmpty() -> {
//                    EmptyPage(stringResource(R.string.chat_gif_list_no_item))
//                }
//                else -> {
//                    GifList(gifs, onGifClick, onLoadMoreGifs = { onLoadMoreGifs(searchText) })
//                }
//            }
//        }
//
//        AnimatedVisibility(
//            !isDisplaySearchBar,
//            enter = fadeIn()
//        ) {
//            FloatingActionButton(
//                onClick = { onExpandedGifList(true) },
//                modifier = Modifier
//                    .padding(start = 16.dp, bottom = 8.dp, top = 180.dp)
//                    .graphicsLayer(alpha = 1 - currentFraction)
//            ) {
//                Icon(
//                    painterResource(R.drawable.ic_search_fill),
//                    contentDescription = null,
//                )
//            }
//        }
//    }
// }
//
// @Composable
// private fun GifList(
//    gifs: List<Gif>,
//    onGifClick: (Gif) -> Unit,
//    onLoadMoreGifs: () -> Unit
// ) {
//    val scrollState = rememberLazyListState()
//
//    Column(
//        modifier = Modifier.verticalScroll(rememberScrollState())
//    ) {
//        StaggeredVerticalGrid(
//            numColumn = 2,
//            modifier = Modifier.padding(2.dp)
//        ) {
//            gifs.forEach { gif ->
//                GifItem(gif = gif, onGifClick = onGifClick)
//            }
//        }
//    }
//
//    InfiniteListHandler(scrollState, onLoadMore = onLoadMoreGifs)
// }
//
// @Composable
// private fun GifItem(
//    gif: Gif,
//    onGifClick: (Gif) -> Unit
// ) {
//    AsyncImage(
//        gif.url,
//        contentDescription = null,
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .padding(6.dp)
//            .clickable { onGifClick(gif) },
//        contentScale = ContentScale.Fit
//    )
// }