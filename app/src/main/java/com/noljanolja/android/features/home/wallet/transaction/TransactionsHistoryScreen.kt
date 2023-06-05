package com.noljanolja.android.features.home.wallet.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.wallet.composable.TimeHeader
import com.noljanolja.android.features.home.wallet.composable.TransactionRow
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.EmptyAnimation
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.DeeperGrey
import com.noljanolja.android.ui.theme.darkContent
import com.noljanolja.android.util.formatMonthAndYear
import com.noljanolja.android.util.getMonth
import com.noljanolja.android.util.getYear
import org.koin.androidx.compose.getViewModel

@Composable
fun TransactionsHistoryScreen(
    viewModel: TransactionHistoryViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WalletTransactionContent(uiState = uiState, handleEvent = viewModel::handleEvent)
}

@Composable
private fun WalletTransactionContent(
    uiState: UiState<TransactionHistoryUiData>,
    handleEvent: (TransactionsHistoryEvent) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(id = R.string.transaction_history),
                onBack = {
                    handleEvent(TransactionsHistoryEvent.Back)
                },
                centeredTitle = true
            )
        },
    ) {
        val uiData = uiState.data ?: return@ScaffoldWithUiState
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                searchText = searchText,
                hint = stringResource(id = R.string.transaction_history_search_hint),
                background = MaterialTheme.colorScheme.surface,
                onSearch = {
                    searchText = it
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TransactionFilterType.All.let {
                    TransactionTypeButton(
                        modifier = Modifier.weight(1F),
                        title = stringResource(id = it.titleId),
                        isSelect = uiState.data.filterType == it
                    ) {
                        handleEvent(TransactionsHistoryEvent.Filter(it))
                    }
                }
                TransactionFilterType.Received.let {
                    TransactionTypeButton(
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .weight(1.3F),
                        title = stringResource(id = it.titleId),
                        isSelect = uiState.data.filterType == it
                    ) {
                        handleEvent(TransactionsHistoryEvent.Filter(it))
                    }
                }
                TransactionFilterType.Spent.let {
                    TransactionTypeButton(
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .weight(1.3F),
                        title = stringResource(id = it.titleId),
                        isSelect = uiState.data.filterType == it
                    ) {
                        handleEvent(TransactionsHistoryEvent.Filter(it))
                    }
                }
//                TransactionFilterType.BuyInShop.let {
//                    TransactionTypeButton(
//                        modifier = Modifier.padding(start = 15.dp).weight(1.3F),
//                        title = stringResource(id = it.titleId),
//                        isSelect = uiState.data.filterType == it
//                    ) {
//                    }
//                }
            }
            SizeBox(height = 16.dp)
            if (uiData.transactions.isNotEmpty()) {
                LazyColumn() {
                    val transactionsByMonth = uiData.transactions.sortedByDescending { it.createdAt }.groupBy {
                        it.createdAt.formatMonthAndYear()
                    }
                    transactionsByMonth.forEach {
                        val time = it.value.first().createdAt
                        item {
                            TimeHeader(time = it.key) {
                                handleEvent(TransactionsHistoryEvent.Dashboard(time.getMonth(), time.getYear()))
                            }
                        }
                        it.value.withIndex().forEach { (index, value) ->
                            item {
                                TransactionRow(
                                    transaction = value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 54.dp)
                                        .clickable {
                                            handleEvent(TransactionsHistoryEvent.Detail(value))
                                        }
                                        .background(if (index % 2 == 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
                                        .padding(vertical = 10.dp, horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }
            } else {
                EmptyAnimation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun TransactionTypeButton(
    modifier: Modifier = Modifier,
    title: String,
    isSelect: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick.invoke() }
            .background(
                if (isSelect) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelect) Color.Transparent else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelect) MaterialTheme.darkContent() else DeeperGrey
        )
    }
}
