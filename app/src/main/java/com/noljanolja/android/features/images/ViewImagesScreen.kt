package com.noljanolja.android.features.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.getUriFromCache
import org.koin.androidx.compose.getViewModel

@Composable
fun ViewImagesScreen(
    images: List<String>,
    viewModel: ViewImagesViewModel = getViewModel(),
) {
    ViewImageContent(
        images = images,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun ViewImageContent(
    images: List<String>,
    handleEvent: (ViewImagesEvent) -> Unit,
) {
    val pagerState = rememberPagerState()
    Scaffold {
        Image(
            painter = painterResource(id = R.drawable.view_image_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SizeBox(height = 30.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        handleEvent(ViewImagesEvent.Back)
                    }
                )
                Expanded()
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                    }
                )
                SizeBox(width = 16.dp)
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                    }
                )
            }
            Text(
                "${pagerState.currentPage + 1}/${images.size}",
                style = MaterialTheme.typography.titleMedium.withMedium()
            )
            ImagePagers(images = images, pagerState = pagerState)
            SizeBox(height = 50.dp)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ImagePagers(images: List<String>, pagerState: PagerState) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    HorizontalPager(
        count = images.size,
        state = pagerState
    ) { page ->
        val uri = context.getUriFromCache(images[page]) ?: images[page]
        Box(
            modifier = Modifier.heightIn(max = (configuration.screenHeightDp - 100).dp)
                .widthIn(max = (configuration.screenWidthDp - 20).dp)
                .padding(horizontal = 16.dp, vertical = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}