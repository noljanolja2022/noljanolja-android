package com.noljanolja.android.features.home.wallet.composable

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
import com.noljanolja.android.features.home.wallet.model.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*

@Composable
fun TransactionRow(
    modifier: Modifier = Modifier,
    textColor: Color,
    timeColor: Color,
    transaction: UiLoyaltyPoint
) {
    val value = transaction.amount
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1F)
        ) {
            Text(
                stringResource(
                    id = if (transaction.type == Type.RECEIVE) {
                        R.string.transactions_history_receive_reason
                    } else {
                        R.string.transactions_history_spent_reason
                    },
                    transaction.reasonLocale
                ),
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
            SizeBox(height = 5.dp)
            Text(
                transaction.createdAt.formatFullTimeTransactionNew(),
                style = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = timeColor
                )
            )
        }
        Text(
            text = transaction.getPoint().plus(" ${transaction.unit}"),
            style = MaterialTheme.typography.labelLarge,
            color = if (value >= 0) {
                MaterialTheme.systemGreen()
            } else {
                MaterialTheme.systemRed100()
            }
        )
    }
}