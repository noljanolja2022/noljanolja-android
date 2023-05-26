package com.noljanolja.android.features.home.wallet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.features.home.wallet.model.Type
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.colorBackground
import com.noljanolja.android.ui.theme.systemGreen
import com.noljanolja.android.ui.theme.systemRed100
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatTransactionFullTime
import org.koin.androidx.compose.getViewModel

@Composable
fun TransactionDetailScreen(
    loyaltyPoint: UiLoyaltyPoint,
    viewModel: TransactionDetailViewModel = getViewModel(),
) {
    TransactionDetailContent(loyaltyPoint = loyaltyPoint, handleEvent = viewModel::handleEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailContent(
    loyaltyPoint: UiLoyaltyPoint,
    handleEvent: (TransactionDetailEvent) -> Unit,
) {
    val isReceive = loyaltyPoint.type == Type.RECEIVE
    Scaffold(
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(id = R.string.transaction_detail),
                onBack = {
                    handleEvent(TransactionDetailEvent.Back)
                },
                centeredTitle = true
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorBackground())
                .padding(vertical = 27.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(
                    id = if (isReceive) {
                        R.string.transaction_receive_type
                    } else {
                        R.string.transaction_spent_type
                    }
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = loyaltyPoint.getPoint(),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (isReceive) MaterialTheme.systemGreen() else MaterialTheme.systemRed100()
                )
            )
            SizeBox(height = 10.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.transaction_detail_status),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    loyaltyPoint.status.name,
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.background),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.systemGreen())
                        .padding(horizontal = 10.dp)
                )
            }
            SizeBox(height = 13.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.transaction_detail_time),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    loyaltyPoint.createdAt.formatTransactionFullTime(),
                    style = MaterialTheme.typography.bodyMedium.withBold()
                )
            }
        }
    }
}