package com.noljanolja.android.features.shop.giftdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.WarningDialog
import com.noljanolja.android.ui.composable.rememberQrBitmapPainter
import com.noljanolja.android.ui.theme.green300
import com.noljanolja.android.ui.theme.systemRed100
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.showError
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.domain.model.Gift
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GiftDetailScreen(
    giftId: String,
    code: String,
    viewModel: GiftDetailViewModel = getViewModel { parametersOf(giftId, code) },
) {
    val context = LocalContext.current
    var showPurchaseDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.buyGiftSuccessEvent) {
        viewModel.buyGiftSuccessEvent.collect {
            showPurchaseDialog = it
        }
    }
    LaunchedEffect(viewModel.errorFlow) {
        viewModel.errorFlow.collect {
            context.showError(it)
        }
    }

    GiftDetailContent(
        uiState = uiState,
        memberInfo = memberInfo,
        handleEvent = viewModel::handleEvent
    )
    WarningDialog(
        isWarning = showPurchaseDialog,
        title = stringResource(id = R.string.common_success),
        content = stringResource(id = R.string.shop_order_coupon_success),
        dismissText = stringResource(id = R.string.shop_later).uppercase(),
        confirmText = stringResource(id = R.string.common_use).uppercase(),
        onDismiss = {
            showPurchaseDialog = false
            viewModel.handleEvent(GiftDetailEvent.Back)
        },
        onConfirm = {
            showPurchaseDialog = false
        }
    )
}

@Composable
private fun GiftDetailContent(
    uiState: UiState<GiftDetailUiData>,
    memberInfo: MemberInfo,
    handleEvent: (GiftDetailEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                onBack = { handleEvent(GiftDetailEvent.Back) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        val gift = data.gift
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = gift.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2F),
                contentScale = ContentScale.FillBounds
            )
            SizeBox(height = 20.dp)
            Text(text = gift.name, fontSize = 22.sp, color = MaterialTheme.secondaryTextColor())
            Text(text = gift.brand.name, style = MaterialTheme.typography.titleLarge)
            Text(
                text = gift.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.secondaryTextColor()
            )
            Divider(modifier = Modifier.padding(vertical = 30.dp))
            if (gift.qrCode.isNotBlank()) {
                PurchasedInfo(gift)
            } else {
                PurchaseInfo(
                    memberInfo = memberInfo,
                    gift = gift,
                    onPurchase = {
                        handleEvent(GiftDetailEvent.Purchase)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.PurchaseInfo(
    memberInfo: MemberInfo,
    gift: Gift,
    onPurchase: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.gift_holding_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            text = stringResource(
                id = R.string.gift_value_point,
                memberInfo.point.formatDigitsNumber()
            ),
            style = MaterialTheme.typography.bodyLarge.withBold()
        )
    }
    SizeBox(height = 8.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.gift_deduction_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            text = stringResource(id = R.string.gift_value_point, gift.price.formatDigitsNumber()),
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = MaterialTheme.systemRed100()
        )
    }
    SizeBox(height = 8.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.gift_remaining_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            text = stringResource(
                id = R.string.gift_value_point,
                (memberInfo.point - gift.price).formatDigitsNumber()
            ),
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
        )
    }
    SizeBox(height = 50.dp)
    Expanded()
    PrimaryButton(
        text = stringResource(id = R.string.gift_purchase).uppercase(),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onPurchase
    )
}

@Composable
private fun ColumnScope.PurchasedInfo(
    gift: Gift,
) {
    Image(
        painter = rememberQrBitmapPainter(gift.qrCode),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(0.5F)
            .align(Alignment.CenterHorizontally),
    )
    SizeBox(height = 15.dp)
    Text(
        text = stringResource(id = R.string.gift_give_code_to_cashier),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.green300(),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}