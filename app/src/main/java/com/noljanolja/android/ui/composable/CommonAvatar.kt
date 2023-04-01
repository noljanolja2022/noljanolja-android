package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.core.user.domain.model.User

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    user: User,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = context)
            .data(user.getAvatarUrl())
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.placeholder_avatar)
            .fallback(R.drawable.placeholder_avatar)
            .build(),
        contentDescription = null,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(modifier),
        contentScale = ContentScale.Crop
    )
}