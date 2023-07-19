package com.noljanolja.android.features.home.root.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.accompanist.pager.rememberPagerState
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.util.openUrl
import com.noljanolja.core.event.domain.model.EventAction
import com.noljanolja.core.event.domain.model.EventBanner

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun EventBannerDialog(
    eventBanners: List<EventBanner>,
    onCheckIn: (EventBanner) -> Unit,
    onCloseBanner: (EventBanner) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val state = rememberPagerState()
    val context = LocalContext.current
    var showPage by remember { mutableStateOf(0) }
    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false
        ),
    ) {
        val eventBanner = eventBanners[showPage]
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
                Box(
                    modifier = Modifier.clickable(enabled = eventBanner.action != EventAction.LINK) {
                        when (eventBanner.action) {
                            EventAction.CHECKIN -> {
                                onDismissRequest()
                                onCheckIn(eventBanner)
                            }

                            else -> Unit
                        }
                    }
                ) {
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
                            modifier = Modifier.align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(bottomEnd = 18.dp, bottomStart = 18.dp)
                        ) {
                            context.openUrl(eventBanner.content)
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
                            onCloseBanner(eventBanner)
                            if (showPage < eventBanners.size - 1) {
                                showPage++
                            } else {
                                onDismissRequest()
                            }
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}