package com.noljanolja.android.features.home.root.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.util.openUrl
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.event.domain.model.EventAction
import com.noljanolja.core.event.domain.model.EventBanner
import com.noljanolja.core.user.domain.model.CheckinProgress

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventBannerDialog(
    eventBanners: List<EventBanner>,
    checkinProgresses: List<CheckinProgress>,
    onCheckIn: (EventBanner) -> Unit,
    onCloseBanner: (EventBanner) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
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
            val contentModifier = if (configuration.screenWidthDp > 500) {
                Modifier.widthIn(max = 400.dp)
            } else {
                Modifier.fillMaxWidth()
            }
            Box(
                modifier = contentModifier
                    .clip(RoundedCornerShape(18.dp))
                    .padding(horizontal = 50.dp)
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
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.banner_placeholder)
                    )

                    if (eventBanner.action == EventAction.LINK) {
                        PrimaryButton(
                            text = stringResource(id = R.string.open_link).uppercase(),
                            modifier = Modifier.align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(bottomEnd = 18.dp, bottomStart = 18.dp)
                        ) {
                            context.openUrl(eventBanner.actionUrl)
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

@Composable
private fun CheckinBannerContent(
    eventBanner: EventBanner,
    checkinProgresses: List<CheckinProgress>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp)
    ) {
        AsyncImage(
            model = eventBanner.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.banner_placeholder)
        )
        SizeBox(height = 12.dp)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    val maxInRow = (checkinProgresses.size + 1) / 2
                    checkinProgresses.forEachIndexed { index, progress ->
                        val isBottom = index >= maxInRow
                        val position =
                            if (index < maxInRow) index else index - maxInRow
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = if (isBottom) {
                                        (position * 90).dp + 45.dp
                                    } else {
                                        (position * 90).dp
                                    }
                                )
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                .background(if (progress.isCompleted) MaterialTheme.colorScheme.error else Color.White)
                                .align(if (!isBottom) Alignment.TopStart else Alignment.BottomStart),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                progress.rewardPoints.toString(),
                                color = MaterialTheme.secondaryTextColor()
                            )
                        }
                    }
                }
            }
        }
    }
}