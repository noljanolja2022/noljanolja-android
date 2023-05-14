package com.noljanolja.android.util

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.noljanolja.android.R
import com.noljanolja.core.loyalty.domain.model.MemberTier

fun MemberTier.getTitle(context: Context): String {
    val titleId: Int
    when (this) {
        MemberTier.BRONZE -> {
            titleId = R.string.wallet_ranking_general
        }

        MemberTier.SILVER -> {
            titleId = R.string.wallet_ranking_general
        }

        MemberTier.GOLD -> {
            titleId = R.string.wallet_ranking_gold
        }

        MemberTier.PREMIUM -> {
            titleId = R.string.wallet_ranking_premium
        }
    }
    return context.getString(titleId)
}

fun MemberTier.getDescription(context: Context): String {
    val descriptionId: Int
    return when (this) {
        MemberTier.BRONZE -> {
            "10% exchange fee, KRW 1,000 when signing up as a referral"
        }

        MemberTier.SILVER -> {
            "10% exchange fee, KRW 1,000 when signing up as a referral"
        }

        MemberTier.GOLD -> {
            "Exchange fee 8%, 1,200 won when signing up as a referral, Special bonus paid to top 5 recommenders"
        }

        MemberTier.PREMIUM -> {
            "Exchange fee 5%, 1,500 won when signing up as a referral, Special bonus payment for top 5 recommenders, monthly bonus payment"
        }
    }
}

@Composable
fun MemberTier.getBackgroundColor(): Color {
    val containerColor: Color
    with(MaterialTheme.colorScheme) {
        when (this@getBackgroundColor) {
            MemberTier.BRONZE -> {
                containerColor = surface
            }

            MemberTier.SILVER -> {
                containerColor = onBackground
            }

            MemberTier.GOLD -> {
                containerColor = onBackground
            }

            MemberTier.PREMIUM -> {
                containerColor = secondary
            }
        }
    }
    return containerColor
}