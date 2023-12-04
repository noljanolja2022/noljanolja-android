package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User

@Composable
fun WalletUserInformation(
    user: User,
    memberInfo: MemberInfo?,
    goToSetting: () -> Unit,
    goToRanking: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar(user = user, size = 64.dp)
        SizeBox(width = 16.dp)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = user.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
//            memberInfo?.currentTier?.let { RankingRow(tier = it, onClick = goToRanking) }
        }
        Expanded()
        IconButton(
            onClick = goToSetting,
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}