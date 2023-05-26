package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.features.home.wallet.model.Type
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.systemGreen
import com.noljanolja.android.ui.theme.systemRed100
import com.noljanolja.android.util.formatFullTime

@Composable
fun TransactionRow(
    modifier: Modifier = Modifier,
    transaction: UiLoyaltyPoint,
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
                    transaction.reason
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            SizeBox(height = 5.dp)
            Text(
                transaction.createdAt.formatFullTime(),
                style = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = stringResource(
                id = R.string.transaction_history_point,
                transaction.getPoint()
            ),
            style = MaterialTheme.typography.labelLarge,
            color = if (value >= 0) {
                MaterialTheme.systemGreen()
            } else {
                MaterialTheme.systemRed100()
            }
        )
    }
}