package com.noljanolja.android.features.home.wallet.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.features.home.wallet.model.Type
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
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
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorBackground())
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MarginVertical(5)
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
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.systemGreen())
                                .padding(horizontal = 10.dp)
                        )
                    }
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_type),
                        value = "Video watching"
                    )
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_time),
                        value = loyaltyPoint.createdAt.formatFullTimeNew()
                    )
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_code),
                        value = "135564564234"
                    )
                }
                MarginVertical(15)
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorBackground())
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MarginVertical(5)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.transaction_detail_video_name),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                    MarginVertical(5)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "개그맨 박준형 “내가 내 아내 김지혜씨와 안싸우는 이유...?” (feat.갈갈이 패밀리)",
                        style = MaterialTheme.typography.bodyMedium.withBold(),
                        textAlign = TextAlign.Start
                    )
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_video_state),
                        value = "10 min/ 10 min",
                        valueStyle = MaterialTheme.typography.bodyMedium
                    )
                    MarginVertical(10)
                    LinearProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = Green100
                    )
                    MarginVertical(10)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "100%",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            Icons.Default.Help,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {

                                }
                        )
                    }
                    MarginVertical(24)
                    ButtonRadius(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        title = stringResource(id = R.string.transaction_detail_video_complete).uppercase(),
                        bgColor = Green100,
                        bgDisableColor = NeutralGrey,
                        onClick = {}
                    )
                }
            }
            MarginVertical(5)
            ButtonRadius(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.transaction_detail_video_send_report).uppercase(),
                textColor = Color.Black,
                bgColor = Green100,
                onClick = {}
            )
        }
    }
}
