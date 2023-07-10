package com.noljanolja.android.features.home.root.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.util.openUrl
import com.noljanolja.core.event.domain.model.EventAction
import com.noljanolja.core.event.domain.model.EventBanner
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun EventBannerDialog(
    eventBanners: List<EventBanner>,
    onDismissRequest: () -> Unit,
) {
    val state = rememberPagerState()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        while (eventBanners.isNotEmpty()) {
            delay(4000)
            val currentPage = state.currentPage
            if (currentPage != eventBanners.size - 1) {
                state.animateScrollToPage(currentPage + 1)
            } else {
                state.animateScrollToPage(0)
            }
        }
    }
    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeutralDarkGrey.copy(alpha = 0.7f))
                .clickable(enabled = false) { },
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .padding(horizontal = 50.dp)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            ) {
                HorizontalPager(count = eventBanners.size, state = state) { page ->
                    val eventBanner = eventBanners[page]
                    Box() {
                        AsyncImage(
                            model = eventBanner.image,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .align(Alignment.Center),
                            contentScale = ContentScale.Crop
                        )
                        if (eventBanner.action == EventAction.LINK) {
                            PrimaryButton(
                                text = "OPEN LINK",
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                context.openUrl(eventBanner.content)
                            }
                        }
                    }
                }

                Icon(
                    Icons.Default.Close,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
                        .clickable {
                            onDismissRequest()
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(eventBanners.size) { index ->
                        val dotColor = if (index == state.currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                        Box(
                            modifier = Modifier.padding(horizontal = 3.dp)
                                .size(6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(dotColor)
                        )
                    }
                }
            }
        }
    }
}