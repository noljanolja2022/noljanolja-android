package com.noljanolja.android.features.shop.giftdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.DividerElevation
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.WarningDialog
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.systemRed100
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatDouble
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.showError
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
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
            }
            if (!isPurchased) {
                DividerElevation()
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background).padding(
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
                myBalance.balance.formatDouble()
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
                (myBalance.balance - gift.price).formatDouble()
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
        modifier = modifier.fillMaxWidth().aspectRatio(1f)
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