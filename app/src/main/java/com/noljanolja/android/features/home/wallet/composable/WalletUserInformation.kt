package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.util.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.user.domain.model.*

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
        CircleAvatar(
            modifier = Modifier.clickable {
                goToSetting()
            },
            user = user,
            size = 64.dp
        )
        SizeBox(width = 16.dp)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.clickable {
                    goToSetting()
                },
                text = user.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            memberInfo?.run {
                RankingRow(tier = currentTier, onClick = goToRanking)
                Text(
                    text = stringResource(id = R.string.overall_point_ranking, point.formatDigitsNumber()),
                    color = Color.Black,
                    fontSize = 10.sp
                )
            }
        }
//        Expanded()
//        IconButton(
//            onClick = goToSetting,
//            modifier = Modifier.align(Alignment.CenterVertically)
//        ) {
//            Icon(
//                Icons.Filled.Settings,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//        }
    }
}