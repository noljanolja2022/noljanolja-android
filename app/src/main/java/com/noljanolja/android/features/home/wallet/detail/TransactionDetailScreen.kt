package com.noljanolja.android.features.home.wallet.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.lifecycle.compose.*
import coil.compose.*
import com.google.gson.*
import com.noljanolja.android.R
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.home.wallet.model.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.shop.domain.model.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

private const val REASON_SEND_MESSAGES = "REASON_SEND_MESSAGES"
private const val REASON_PURCHASE_GIFT = "REASON_PURCHASE_GIFT"
private const val REASON_EXCHANGE_POINT = "REASON_EXCHANGE_POINT"
private const val REASON_WATCH_VIDEO = "REASON_EXCHANGE_POINT"

@Composable
fun TransactionDetailScreen(
    loyaltyPoint: UiLoyaltyPoint,
    viewModel: TransactionDetailViewModel = getViewModel {
        parametersOf(loyaltyPoint.id, loyaltyPoint.reason)
    }
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TransactionDetailContent(
        loyaltyPoint = uiState,
        handleEvent = viewModel::handleEvent
    )
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
                        text = loyaltyPoint.run { getPoint().plus(" $unit") },
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
                        value = loyaltyPoint.reasonLocale
                    )
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_time),
                        value = loyaltyPoint.createdAt.formatFullTimeTransactionDetailNew()
                    )
                    MarginVertical(10)
                    TextViewTitle(
                        title = stringResource(id = R.string.transaction_detail_code),
                        value = loyaltyPoint.id
                    )
                }
                MarginVertical(15)
                if (loyaltyPoint.log.isNotBlank()) {
                    when (loyaltyPoint.reason) {
                        REASON_PURCHASE_GIFT -> {
                            GiftDetailView(
                                type = stringResource(id = R.string.transaction_detail_video_e_voucher),
                                gift = Gson().fromJson(loyaltyPoint.log, Gift::class.java)
                            )
                        }

                        else -> {}
                    }
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

@Composable
private fun GiftDetailView(
    modifier: Modifier = Modifier,
    type: String,
    gift: Gift
) {
    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorBackground())
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        val (tvType, img, tvName, tvBrand, line) = createRefs()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(tvType) {
                    top.linkTo(parent.top)
                },
            text = type,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
        )
        AsyncImage(
            model = gift.image,
            contentDescription = null,
            modifier = Modifier
                .size((55 * getScaleSize()).dp)
                .clip(
                    RoundedCornerShape(5)
                )
                .background(Color.White)
                .padding(5.dp)
                .constrainAs(img) {
                    start.linkTo(parent.start)
                    linkTo(
                        top = tvType.bottom,
                        topMargin = 5.dp,
                        bottom = parent.bottom
                    )
                }
        )
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .constrainAs(line) {
                    linkTo(
                        top = img.top,
                        bottom = img.bottom
                    )
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = gift.name,
            style = MaterialTheme.typography.labelSmall.withBold(),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .constrainAs(tvName) {
                    linkTo(
                        top = img.top,
                        bottom = line.top,
                        bias = 1F
                    )
                    linkTo(
                        start = img.end,
                        startMargin = 5.dp,
                        end = parent.end
                    )
                    width = Dimension.fillToConstraints
                },
        )
        Text(
            text = gift.brand.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .constrainAs(tvBrand) {
                    linkTo(
                        top = tvName.bottom,
                        bottom = img.bottom,
                        bias = 0F
                    )
                    linkTo(
                        start = img.end,
                        startMargin = 5.dp,
                        end = parent.end,
                    )
                    width = Dimension.fillToConstraints
                },
        )
    }
}

@Composable
private fun VideoDetailView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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

