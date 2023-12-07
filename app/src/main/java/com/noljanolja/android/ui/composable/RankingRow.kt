package com.noljanolja.android.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.features.home.wallet.composable.TierIcon
import com.noljanolja.android.util.getBackgroundColor
import com.noljanolja.android.util.getTitle
import com.noljanolja.core.loyalty.domain.model.MemberTier

@Composable
fun RankingRow(
    modifier: Modifier = Modifier,
    tier: MemberTier,
    onClick: () -> Unit
) {
    val containerColor: Color = tier.getBackgroundColor()
    val contentColor: Color
    val context = LocalContext.current
    with(MaterialTheme.colorScheme) {
        when (tier) {
            MemberTier.BRONZE -> {
                contentColor = onBackground
            }

            MemberTier.SILVER -> {
                contentColor = background
            }

            MemberTier.GOLD -> {
                contentColor = background
            }

            MemberTier.PREMIUM -> {
                contentColor = background
            }
        }
    }
    Row(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TierIcon(tier = tier, width = 20.dp)
        SizeBox(width = 4.dp)
        Text(
            text = tier.getTitle(context),
            style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = contentColor,
                fontWeight = FontWeight.Medium
            ),
        )
    }
}