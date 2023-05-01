package com.noljanolja.android.features.home.wallet.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.ui.composable.SizeBox
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletDashboardScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 16.dp)
    ) {
        WalletDashboardContent()
    }
}

@Composable
private fun WalletDashboardContent() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        TransactionsChart()
        RecentTransactions()
    }
}

@Composable
private fun TransactionsChart() {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Chart(
            chart = columnChart(
                columns = listOf(
                    LineComponent(
                        thicknessDp = 4f,
                        color = MaterialTheme.colorScheme.primary.toArgb()
                    ),
                    LineComponent(
                        thicknessDp = 4f,
                        color = MaterialTheme.colorScheme.error.toArgb()
                    ),
                ),
                innerSpacing = 0.dp,
            ),
            model = entryModelOf(
                listOf(FloatEntry(1f, 3000000f), FloatEntry(2f, 4000000f)),
                listOf(FloatEntry(1f, 3000000f), FloatEntry(2f, 5000000f)),
            ),
            startAxis = startAxis(
                valueFormatter = { value, _ ->
                    "${(value / 1000).toInt()}"
                }
            ),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    "Hello $value"
                }
            ),
        )
    }
}

@Composable
private fun RecentTransactions() {
    val paddingHorizontalModifier = Modifier.padding(horizontal = 12.dp)
    LazyColumn(
        modifier = Modifier
            .clip(
                RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.background)

    ) {
        item {
            Text(
                "Recent transactions",
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
            )
            SizeBox(height = 9.dp)
            Text(
                text = "Wednesday",
                modifier = paddingHorizontalModifier,
                style = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                "March 1st, 2023",
                style = MaterialTheme.typography.labelMedium,
                modifier = paddingHorizontalModifier
            )
        }

        items(count = 5) { index ->
            if (index != 0) {
                Divider(thickness = 1.dp)
            }
            SizeBox(height = 8.dp)
            Row(
                modifier = paddingHorizontalModifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Buy Emoticon set",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    "- 15000",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            SizeBox(height = 8.dp)
        }
    }
}