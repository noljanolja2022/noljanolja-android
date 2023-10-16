package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.core.user.domain.model.User

@Composable
fun OvalAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    user: User,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = context)
            .data(user.getAvatarUrl())
            .placeholder(R.drawable.placeholder_account)
            .error(R.drawable.placeholder_account)
            .fallback(R.drawable.placeholder_account)
            .build(),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun CircleAvatar(
    size: Dp = 40.dp,
    user: User,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = context)
            .data(user.getAvatarUrl())
            .placeholder(R.drawable.placeholder_account)
            .error(R.drawable.placeholder_account)
            .fallback(R.drawable.placeholder_account)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun CircleAvatar(
    size: Dp = 40.dp,
    avatar: String?,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = context)
            .data(avatar)
            .placeholder(R.drawable.placeholder_account)
            .error(R.drawable.placeholder_account)
            .fallback(R.drawable.placeholder_account)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun OvalAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    avatar: String?,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = context)
            .data(avatar)
            .placeholder(R.drawable.placeholder_account)
            .error(R.drawable.placeholder_account)
            .fallback(R.drawable.placeholder_account)
            .build(),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3)),
        contentScale = ContentScale.Crop
    )
}