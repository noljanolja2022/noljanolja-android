package com.noljanolja.android.features.shop.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.CouponItem
import com.noljanolja.android.features.shop.composable.HelpDialog
import com.noljanolja.android.features.shop.composable.ProductItem
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.PrimaryGreen
import com.noljanolja.android.ui.theme.textColor
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    ShopContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ShopContent(
    uiState: UiState<ShopUiData>,
    handleEvent: (ShopEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState
    ) {
        val state = rememberPullRefreshState(uiState.loading, { handleEvent(ShopEvent.Refresh) })

        val data = uiState.data ?: return@ScaffoldWithUiState

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pullRefresh(state)
        ) {
            SearchProductHeader(
                goToSearch = { handleEvent(ShopEvent.Search) },
            )
            SizeBox(height = 15.dp)
            MyCash(
                myBalance = uiState.data.myBalance,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            SizeBox(height = 20.dp)
            ProductsAndVouchers(
                gifts = data.gifts,
                myGifts = data.myGifts,
                onItemClick = {
                    handleEvent(ShopEvent.GiftDetail(it.id, it.qrCode))
                },
                onUse = {
                    handleEvent(
                        ShopEvent.GiftDetail(
                            it.giftId(),
                            it.qrCode
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchProductHeader(
    goToSearch: () -> Unit,
) {
    var isShowHelp by remember {
        mutableStateOf(false)
    }
    var topHelpPosition by remember { mutableStateOf(0F) }

    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.shop_welcome_nolja_shop),
                style = MaterialTheme.typography.titleSmall.withMedium(),
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                Icons.Default.Help,
                contentDescription = null,
                tint = if (isShowHelp) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        isShowHelp = true
                    }
                    .onGloballyPositioned { coordinates ->
                        val y = coordinates.positionInRoot().y
                        topHelpPosition = y
                    }
            )
        }
        SizeBox(height = 10.dp)
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.0.sp,
                        color = textColor()
                    )
                ) {
                    append("Exchange The ")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.0.sp,
                        fontWeight = FontWeight(700),
                        color = Orange300
                    )
                ) {
                    append("Best")
                }
            }
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.0.sp,
                        fontWeight = FontWeight(700),
                        color = PrimaryGreen
                    )
                ) {
                    append("Product")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.0.sp,
                        color = textColor()
                    )
                ) {
                    append(" With ")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.0.sp,
                        fontWeight = FontWeight(700),
                        color = Orange300
                    )
                ) {
                    append("Cash")
                }
            }
        )
        SizeBox(height = 15.dp)
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { goToSearch.invoke() },
            searchText = "",
            hint = stringResource(id = R.string.shop_search_products),
            onSearch = {},
            enabled = false,
        )
    }
    HelpDialog(
        visible = isShowHelp,
        topPosition = topHelpPosition
    ) {
        isShowHelp = false
    }
}

fun LazyListScope.shop(
    gifts: List<Gift>,
    onItemClick: (Gift) -> Unit,
) {
    items(count = (gifts.size + 1) / 2) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ProductItem(
                gift = gifts[row * 2],
                modifier = Modifier
                    .weight(1F),
                onClick = {
                    onItemClick.invoke(it)
                }
            )
            SizeBox(width = 12.dp)
            gifts.getOrNull(row * 2 + 1)?.let {
                ProductItem(
                    gift = it,
                    modifier = Modifier
                        .weight(1F),
                    onClick = {
                        onItemClick.invoke(it)
                    }
                )
            } ?: Box(
                modifier = Modifier
                    .weight(1F)
            )
        }
        SizeBox(height = 12.dp)
    }
}

private fun LazyListScope.vouchers(
    myGifts: List<Gift>,
    onUse: (Gift) -> Unit,
) {
    items(count = (myGifts.size + 1) / 2) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            CouponItem(
                gift = myGifts[row * 2],
                modifier = Modifier
                    .weight(1F),
                onUse = {
                    onUse(myGifts[row * 2])
                }
            )
            SizeBox(width = 12.dp)
            myGifts.getOrNull(row * 2 + 1)?.let {
                CouponItem(
                    gift = it,
                    modifier = Modifier
                        .weight(1F),
                    onUse = {
                        onUse.invoke(it)
                    }
                )
            } ?: Box(
                modifier = Modifier
                    .weight(1F)
            )
        }
        SizeBox(height = 12.dp)
    }
}

@Composable
private fun MyCash(
    myBalance: ExchangeBalance,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painterResource(R.drawable.wallet_cash_card),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Expanded()
                Text(
                    stringResource(R.string.my_cash),
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = NeutralDarkGrey
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wallet_ic_coin),
                        contentDescription = null,
                        modifier = Modifier.size(37.dp)
                    )
                    SizeBox(width = 10.dp)
                    Text(
                        text = myBalance.balance.toString(),
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDarkGrey
                        )
                    )
                }
                Expanded()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ProductsAndVouchers(
    gifts: List<Gift>,
    myGifts: List<Gift>,
    onItemClick: (Gift) -> Unit,
    onUse: (Gift) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Shop",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal
            )
            if (pagerState.currentPage == 0) {
                Divider(
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "My E-Vouchers",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Normal
            )
            if (pagerState.currentPage == 1) {
                Divider(
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
    SizeBox(height = 15.dp)
    HorizontalPager(
        count = 2,
        state = pagerState,
    ) { page ->
        if (page == 0) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                shop(
                    gifts = gifts,
                    onItemClick = onItemClick
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                vouchers(
                    myGifts = myGifts,
                    onUse = onUse
                )
            }
        }
    }
}