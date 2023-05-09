package com.noljanolja.android.features.home.wallet.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.noljanolja.android.features.home.wallet.composable.TimeHeader
import com.noljanolja.android.features.home.wallet.composable.TransactionRow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

@Composable
fun WalletTransactionScreen() {
    WalletTransactionContent()
}

@Composable
private fun WalletTransactionContent() {
    val header = testData.sortedByDescending { it.date }.groupBy { it.date }
    LazyColumn() {
        header.forEach {
            item {
                TimeHeader(time = it.key) {
                }
                Divider(color = MaterialTheme.colorScheme.primary)
            }
            it.value.withIndex().forEach {
                item {
                    TransactionRow(index = it.index)
                }
            }
        }
    }
}

data class TestTransaction(
    val text: String,
    val date: Instant,
)

val testData = listOf(
    TestTransaction("Hello", Clock.System.now().minus(90.days)),
    TestTransaction("Hello1", Clock.System.now().minus(45.days)),
    TestTransaction("Hello2", Clock.System.now().minus(30.days)),
    TestTransaction("Hello3", Clock.System.now().minus(30.days)),
)