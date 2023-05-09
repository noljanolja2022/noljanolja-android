package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.formatTransactionFullTime
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

@Composable
fun TransactionRow(
    index: Int,
) {
    val time = Clock.System.now().minus(Random.nextInt().days)
    val value = Math.pow(-1.0, index.toDouble()) * Random.nextInt()
    Row(
        modifier = Modifier.fillMaxWidth().height(54.dp)
            .background(if (index % 2 == 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1F)
        ) {
            Text("Received: Video watching", style = MaterialTheme.typography.labelSmall)
            SizeBox(height = 5.dp)
            Text(
                time.formatTransactionFullTime(),
                style = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = "$value points",
            style = MaterialTheme.typography.labelLarge,
            color = if (value >= 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}