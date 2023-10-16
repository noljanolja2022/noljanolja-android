package com.noljanolja.android.features.home.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonListTile
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.PrimaryDivider
import com.noljanolja.android.ui.composable.PrimaryListTile
import com.noljanolja.android.util.primaryTextColor
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.user.domain.model.displayIdentity
import org.koin.androidx.compose.getViewModel

@Composable
fun MenuScreen(
    viewModel: MenuViewModel = getViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MenuContent(uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuContent(
    uiState: MenuUIState,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.menu_title),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(vertical = 18.dp, horizontal = 20.dp),
        ) {
            AccountSection(uiState)
            PrimaryDivider(
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            )
            MenuItemSection(
                title = stringResource(id = R.string.menu_announcement),
                iconDrawable = R.drawable.ic_announce,
                onClick = {},
            )
//            MenuItemSection(
//                title = stringResource(id = R.string.menu_play_video),
//                iconDrawable = R.drawable.ic_youtube,
//                onClick = {},
//            )
//            MenuItemSection(
//                title = stringResource(id = R.string.menu_join_and_play),
//                iconDrawable = R.drawable.ic_user_circle,
//                onClick = {},
//            )
//            MenuItemSection(
//                title = stringResource(id = R.string.menu_checkout_and_play),
//                iconDrawable = R.drawable.ic_calendar,
//                onClick = {},
//            )
            MenuItemSection(
                title = stringResource(id = R.string.menu_point_details),
                iconDrawable = R.drawable.ic_point,
                onClick = {},
            )
            MenuItemSection(
                title = stringResource(id = R.string.menu_exchange_money),
                iconDrawable = R.drawable.ic_exchange,
                onClick = {},
            )
        }
    }
}

@Composable
private fun AccountSection(
    uiState: MenuUIState,
) {
    val loading = uiState.loading
    val user = uiState.user
    CommonListTile(
        modifier = Modifier,
        leading = {
            if (loading) {
                CircularProgressIndicator()
            } else {
                val modifier = Modifier
                    .padding(end = 14.dp)
                    .size(62.dp)
                    .clip(CircleShape)
                user?.avatar?.takeIf { it.isNotBlank() }?.let { image ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(
                            image,
                        ).crossfade(true).build(),
                        contentDescription = "avatar",
                        placeholder = painterResource(id = R.drawable.placeholder_account),
                        contentScale = ContentScale.FillWidth,
                        modifier = modifier,
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.placeholder_account),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
        },
        title = {
            Text(
                user.displayIdentity().takeIf { !loading }.orEmpty(),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.primaryTextColor(),
                ),
            )
        },
        description = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Recommended",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.secondaryTextColor(),
                    ),
                )
                Image(
                    painterResource(id = R.drawable.ic_crown),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp),
                )
            }
        },
    )
}

@Composable
private fun MenuItemSection(
    @DrawableRes iconDrawable: Int,
    title: String,
    onClick: () -> Unit,
) {
    PrimaryListTile(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .padding(bottom = 12.dp),
        title = {
            Text(
                text = title,
                style = TextStyle(
                    color = MaterialTheme.secondaryTextColor(),
                    fontSize = 16.sp,
                ),
            )
        },
        leadingDrawable = iconDrawable,
        trailingDrawable = R.drawable.ic_forward,
        onClick = onClick,
    )
}
