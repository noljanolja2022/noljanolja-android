package com.noljanolja.android.features.chatsettings.composable

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.RankingRow
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User

@Composable
fun ChatProfile(
    user: User,
    avatar: Uri?,
    memberInfo: MemberInfo,
    onChangeAvatar: () -> Unit,
) {
    Row {
        avatar?.let {
            SubcomposeAsyncImage(
                avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } ?: CircleAvatar(user = user, size = 52.dp)
        SizeBox(width = 10.dp)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(user.name)
            SizeBox(height = 2.dp)
            RankingRow(tier = memberInfo.currentTier, onClick = {})
        }
    }
    SizeBox(height = 10.dp)
    Box(
        modifier = Modifier
            .heightIn(min = 26.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                onChangeAvatar.invoke()
            }
            .padding(vertical = 3.dp, horizontal = 13.dp)
    ) {
        Text(
            text = stringResource(id = R.string.chat_settings_change_avatar),
            modifier = Modifier.align(
                Alignment.Center,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}