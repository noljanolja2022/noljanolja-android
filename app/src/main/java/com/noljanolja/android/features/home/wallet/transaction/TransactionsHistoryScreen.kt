package com.noljanolja.android.features.home.wallet.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.wallet.composable.TimeHeader
import com.noljanolja.android.features.home.wallet.composable.TransactionRow
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import org.koin.androidx.compose.get

@Composable
fun TransactionsHistoryScreen(
    viewModel: TransactionHistoryViewModel = get(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WalletTransactionContent(uiState = uiState, handleEvent = viewModel::handleEvent)
}

@Composable
private fun WalletTransactionContent(
    uiState: UiState<TransactionHistoryUiData>,
    handleEvent: (TransactionsHistoryEvent) -> Unit,
) {
    val searchText by remember { mutableStateOf("") }
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = "Transaction History",
                onBack = {
                    handleEvent(TransactionsHistoryEvent.Back)
                }
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                searchText = searchText,
                hint = "Search transaction",
                background = MaterialTheme.colorScheme.background
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TransactionFilterType.All.let {
                    TransactionTypeButton(
                        modifier = Modifier.weight(1F),
                        title = it.name,
                        isSelect = uiState.data?.filterType == it
                    ) {
                    }
                }
                TransactionFilterType.Received.let {
                    TransactionTypeButton(
                        modifier = Modifier.padding(start = 15.dp).weight(1.3F),
                        title = it.name,
                        isSelect = uiState.data?.filterType == it
                    ) {
                    }
                }
                TransactionFilterType.Exchange.let {
                    TransactionTypeButton(
                        modifier = Modifier.padding(start = 15.dp).weight(1.3F),
                        title = it.name,
                        isSelect = uiState.data?.filterType == it
                    ) {
                    }
                }
                TransactionFilterType.BuyInShop.let {
                    TransactionTypeButton(
                        modifier = Modifier.padding(start = 15.dp).weight(1.3F),
                        title = it.name,
                        isSelect = uiState.data?.filterType == it
                    ) {
                    }
                }
            }
            SizeBox(height = 16.dp)
            LazyColumn() {
                val header = uiState.data!!.transactions.sortedByDescending { it.createdAt }.groupBy { it.createdAt }
                header.forEach {
                    item {
                        TimeHeader(time = it.key) {
                        }
                        Divider(color = MaterialTheme.colorScheme.primary)
                    }
                    it.value.withIndex().forEach { (index, value) ->
                        item {
                            TransactionRow(
                                transaction = value,
                                modifier = Modifier.fillMaxWidth().height(54.dp)
                                    .background(if (index % 2 == 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                            )
                        }
                    }
                }
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
            color = if (isSelect) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
        )
    }
}
