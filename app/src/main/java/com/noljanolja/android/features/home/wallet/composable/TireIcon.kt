package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.theme.Bronze
import com.noljanolja.android.ui.theme.NeutralGrey
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.core.loyalty.domain.model.MemberTier

@Composable
fun TierIcon(tier: MemberTier, width: Dp = 24.dp) {
    val iconColor: Color
    with(MaterialTheme.colorScheme) {
        when (tier) {
            MemberTier.BRONZE -> {
                iconColor = Bronze
            }

            MemberTier.SILVER -> {
                iconColor = NeutralGrey
            }

            MemberTier.GOLD -> {
                iconColor = secondary
            }

            MemberTier.PREMIUM -> {
                iconColor = Orange300
            }
        }
    }
    Image(
        painterResource(id = R.drawable.ic_king),
        contentDescription = null,
        modifier = Modifier.width(width),
        colorFilter = ColorFilter.tint(iconColor),
        contentScale = ContentScale.FillWidth
    )
}