package com.noljanolja.android.features.shop.giftdetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import coil.compose.*
import coil.request.*
import com.airbnb.lottie.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.features.shop.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.shop.domain.model.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

@Composable
fun GiftDetailScreen(
    giftId: String,
    code: String,
    viewModel: GiftDetailViewModel = getViewModel { parametersOf(giftId, code) },
) {
    val context = LocalContext.current
    var showPurchaseDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val myBalance by viewModel.myBalanceFlow.collectAsStateWithLifecycle()

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
        myBalance = myBalance,
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
    myBalance: ExchangeBalance,
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
        val isPurchased = gift.qrCode.isNotBlank()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                GiftImage(
                    gift = data.gift,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = gift.name,
                        fontSize = 22.sp,
                        color = MaterialTheme.secondaryTextColor()
                    )
                    Text(text = gift.brand.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = gift.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.secondaryTextColor()
                    )
                    SizeBox(height = 15.dp)
                }
                Divider(thickness = 1.dp)

                if (!isPurchased) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    ) {
                        PurchaseInfo(myBalance = myBalance, gift = gift)
                    }
                }

                if (data.giftsByCategory.isNotEmpty()) {
                    MarginVertical(15)

                    Column {
                        SectionTitle(
                            title = stringResource(id = R.string.shop_section_maybe_you_like),
                            icon = Icons.Default.NavigateNext
                        )
                        data.giftsByCategory.forEach { item ->
                            GiftItem(
                                gift = item,
                                onClick = {
                                    handleEvent(
                                        GiftDetailEvent.GiftDetail(
                                            it.id,
                                            it.qrCode
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
            if (!isPurchased) {
                DividerElevation()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 24.dp
                        ),
                ) {
                    PrimaryButton(
                        text = stringResource(id = R.string.gift_purchase).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            handleEvent(GiftDetailEvent.Purchase)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.PurchaseInfo(
    myBalance: ExchangeBalance,
    gift: Gift,
) {
    SizeBox(height = 15.dp)
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
            text = stringResource(id = R.string.gift_value_coin, gift.price.formatDouble()),
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
            text = stringResource(id = R.string.gift_holding_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            text = stringResource(
                id = R.string.gift_value_coin,
                myBalance.balance.toInt().toString(),
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
            text = stringResource(id = R.string.gift_remaining_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            text = stringResource(
                id = R.string.gift_value_coin,
                (myBalance.balance - gift.price).toInt().toString()
            ),
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            color = Orange300
        )
    }
}

@Composable
private fun GiftImage(
    gift: Gift,
    modifier: Modifier = Modifier,
) {
    val image = gift.qrCode.takeIf { it.isNotBlank() } ?: gift.image
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current)
            .data(image)
            .build(),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (gift.qrCode.isBlank()) {
            SubcomposeAsyncImageContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.background),
                contentScale = ContentScale.FillWidth
            )
        } else {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    LoadingImage()
                }

                is AsyncImagePainter.State.Error -> {
                    ImageError()
                }

                else -> SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.background),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
private fun LoadingImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ImageError(modifier: Modifier = Modifier) {
    Box {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_error_404))
        val progress by animateLottieCompositionAsState(composition)
        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            composition = composition,
            progress = { progress }
        )
    }
}