package com.noljanolja.android.features.home.wallet.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.wallet.model.Type
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.colorBackground
import com.noljanolja.android.ui.theme.systemGreen
import com.noljanolja.android.ui.theme.systemRed100
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatMonthYear
import com.noljanolja.android.util.formatNumber
import com.noljanolja.android.util.formatTransactionShortTime
import com.noljanolja.android.util.getDayOfMonth
import com.noljanolja.android.util.getDayOfWeek
import com.noljanolja.android.util.getLastDay
import com.noljanolja.android.util.primaryTextColor
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatrick.vico.core.component.shape.cornered.RoundedCornerTreatment
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.datetime.Month
import org.koin.androidx.compose.getViewModel
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletDashboardScreen(
    month: Int,
    year: Int,
    viewModel: WalletDashboardViewModel = getViewModel(),
) {
    LaunchedEffect(key1 = true, block = {
        viewModel.setTime(month, year)
    })
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WalletDashboardContent(uiState = uiState, handleEvent = viewModel::handleEvent)
}

@Composable
private fun WalletDashboardContent(
    uiState: UiState<DashboardUiData>,
    handleEvent: (WalletDashboardEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = stringResource(id = R.string.wallet_dashboard_title),
                onBack = {
                    handleEvent(WalletDashboardEvent.Back)
                },
                centeredTitle = true
            )
        },
    ) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        val transactions = with(data) { transactions["$currentMonth-$currentYear"] }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 6.dp, horizontal = 16.dp)
        ) {
            LazyColumn() {
                item {
                    TransactionsChart(
                        month = data.currentMonth,
                        year = data.currentYear,
                        transactions = transactions.orEmpty()
                    )
                    SizeBox(height = 5.dp)
                }
                recentTransactions(transactions.orEmpty())
            }
        }
    }
}

@Composable
private fun TransactionsChart(
    month: Int,
    year: Int,
    transactions: List<UiLoyaltyPoint>,
) {
    val periodsInMonth = listOf(1f, 2f, 3f, 4f)
    val transactionsByWeek = transactions.groupBy {
        it.createdAt.let { createdAt ->
            when (createdAt.getDayOfMonth()) {
                in 1..7 -> periodsInMonth[0]
                in 8..15 -> periodsInMonth[1]
                in 16..23 -> periodsInMonth[2]
                else -> periodsInMonth[3]
            }
        }
    }

    val positiveTransactions = mutableListOf<FloatEntry>()
    val negativeTransactions = mutableListOf<FloatEntry>()
    periodsInMonth.forEach { key ->
        val entries = transactionsByWeek[key]
        positiveTransactions.add(
            FloatEntry(
                key,
                entries?.mapNotNull { it.amount.takeIf { amount -> amount >= 0 } }.takeIf { !it.isNullOrEmpty() }
                    ?.reduce { acc, loyaltyPoint -> acc + loyaltyPoint }
                    ?.toFloat() ?: 0f
            )
        )
        negativeTransactions.add(
            FloatEntry(
                key,
                abs(
                    entries?.mapNotNull { it.amount.takeIf { amount -> amount < 0 } }.takeIf { !it.isNullOrEmpty() }
                        ?.reduce { acc, loyaltyPoint -> acc + loyaltyPoint }
                        ?.toFloat() ?: 0f
                )
            )
        )
    }
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 13.dp, vertical = 10.dp)
    ) {
        Text(
            formatMonthYear(month, year),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        SizeBox(height = 36.dp)
        Chart(
            modifier = Modifier.height(150.dp),
            chart = columnChart(
                columns = listOf(
                    LineComponent(
                        thicknessDp = 16f,
                        color = MaterialTheme.systemGreen().toArgb(),
                        shape = CorneredShape(
                            topLeft = Corner.Relative(20, RoundedCornerTreatment),
                            topRight = Corner.Relative(20, RoundedCornerTreatment),
                        )
                    ),
                    LineComponent(
                        thicknessDp = 16f,
                        color = MaterialTheme.systemRed100().toArgb(),
                        shape = CorneredShape(
                            topLeft = Corner.Relative(20, RoundedCornerTreatment),
                            topRight = Corner.Relative(20, RoundedCornerTreatment),
                        )
                    ),
                ),
                innerSpacing = 0.dp,
            ),
            model = entryModelOf(
                positiveTransactions,
                negativeTransactions
            ),
            startAxis = startAxis(
                label = TextComponent.Builder()
                    .apply {
                        textSizeSp = 6F
                        color = MaterialTheme.primaryTextColor().toArgb()
                        margins = MutableDimensions(0f, 0f, 10f, 0f)
                    }.build(),
                valueFormatter = { value, _ ->
                    value.toLong().formatNumber()
                },
                guideline = null,
                tickLength = 0.dp,
                maxLabelCount = 8,
            ),
            bottomAxis = bottomAxis(
                label = TextComponent.Builder()
                    .apply {
                        textSizeSp = 6F
                        color = MaterialTheme.primaryTextColor().toArgb()
                        margins = MutableDimensions(0f, 6f, 0f, 0f)
                    }.build(),
                guideline = null,
                valueFormatter = { value, _ ->
                    when (value) {
                        1f -> "${Month(month)} 1st-7th"
                        2f -> "${Month(month)} 8th-15th"
                        3f -> "${Month(month)} 16th-23th"
                        else -> "${Month(month)} 24th-${month.getLastDay(year)}th"
                    }
                },
                tickLength = 0.dp
            ),
        )
        SizeBox(height = 10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(15.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.systemGreen())
            )
            Text(
                text = stringResource(id = R.string.wallet_dashboard_receive),
                style = TextStyle(fontSize = 6.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
                modifier = Modifier.width(40.dp)
            )
        }
        SizeBox(height = 8.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(15.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.systemRed100())
            )
            Text(
                text = stringResource(id = R.string.wallet_dashboard_spent),
                style = TextStyle(fontSize = 6.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

private fun LazyListScope.recentTransactions(transactions: List<UiLoyaltyPoint>) {
    val sortedTransactions = transactions.sortedByDescending { it.createdAt }
    item {
        Text(
            stringResource(id = R.string.wallet_dashboard_recent_transactions),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.withBold()
        )
    }
    items(sortedTransactions) { transaction ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Text(
                "${transaction.createdAt.getDayOfWeek()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                transaction.createdAt.formatTransactionShortTime(),
                style = MaterialTheme.typography.bodyMedium.withBold()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(45.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(MaterialTheme.colorBackground())
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    id = if (transaction.type == Type.Receive) {
                        R.string.transactions_history_receive_reason
                    } else {
                        R.string.transactions_history_spent_reason
                    },
                    transaction.reason
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    id = R.string.transaction_history_point,
                    transaction.getPoint()
                ),
                style = MaterialTheme.typography.bodySmall.withBold(),
                color = if (transaction.amount > 0) {
                    MaterialTheme.systemGreen()
                } else {
                    MaterialTheme.systemRed100()
                }
            )
        }
        SizeBox(height = 10.dp)
    }
}