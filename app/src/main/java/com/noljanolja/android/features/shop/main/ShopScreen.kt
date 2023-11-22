package com.noljanolja.android.features.shop.main

import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.CouponItem
import com.noljanolja.android.features.shop.composable.GiftItem
import com.noljanolja.android.features.shop.composable.HelpDialog
import com.noljanolja.android.features.shop.composable.MyCashAndVoucher
import com.noljanolja.android.features.shop.composable.ProductItem
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.Constant.*
import com.noljanolja.core.commons.*
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.shop.domain.model.Gift
import org.koin.androidx.compose.getViewModel

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
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
                .background(MaterialTheme.shopBackground())
                .pullRefresh(state)
        ) {
            SearchProductHeader(
                goToSearch = { handleEvent(ShopEvent.Search) },
            )
            SizeBox(height = 20.dp)
            MyCashAndVoucher(myBalance = uiState.data.myBalance) {
                handleEvent(ShopEvent.ViewAllCoupons)
            }
            if (uiState.data.category.isNotEmpty()) {
                MarginVertical(15)
                ListTypes(
                    types = uiState.data.category,
                    onItemClick = {
                        handleEvent(
                            ShopEvent.ViewGiftType(
                                categoryId = it.id,
                                categoryName = it.name
                            )
                        )
                    }
                )
            }
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
            .background(MaterialTheme.shopBackground())
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
        SizeBox(height = 8.dp)
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

fun LazyListScope.giftItems(
    gifts: List<Gift>,
    onItemClick: (Gift) -> Unit,
) {
    items(gifts) {
        GiftItem(
            gift = it,
            onClick = onItemClick,
        )
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
                },
                containerColor = MaterialTheme.shopItemBackground()
            )
            SizeBox(width = 12.dp)
            gifts.getOrNull(row * 2 + 1)?.let {
                ProductItem(
                    gift = it,
                    modifier = Modifier
                        .weight(1F),
                    onClick = onItemClick,
                    containerColor = MaterialTheme.shopItemBackground()
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
                },
                containerColor = MaterialTheme.shopItemBackground()
            )
            SizeBox(width = 12.dp)
            myGifts.getOrNull(row * 2 + 1)?.let {
                CouponItem(
                    gift = it,
                    modifier = Modifier
                        .weight(1F),
                    onUse = {
                        onUse.invoke(it)
                    },
                    containerColor = MaterialTheme.shopItemBackground()
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 19.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.my_cash),
                style = MaterialTheme.typography.bodyLarge.withBold(),
                color = Orange300
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
                    text = myBalance.balance.toInt().toString(),
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

@Composable
private fun MyVouchers(
    giftCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 19.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.shop_coupon),
                style = MaterialTheme.typography.bodyLarge.withBold(),
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_wallet_sharp),
                    contentDescription = null,
                    modifier = Modifier.size(37.dp)
                )
                SizeBox(width = 10.dp)
                Text(
                    text = "$giftCount +",
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

@Composable
private fun ProductsAndVouchers(
    gifts: List<Gift>,
    myGifts: List<Gift>,
    onItemClick: (Gift) -> Unit,
    onUse: (Gift) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ProductSectionList(
                gifts = gifts,
                title = stringResource(id = R.string.shop_section_top_feature),
                containerColor = PrimaryGreen,
                titleColor = Color.Black,
                paddingTop = DefaultValue.PADDING_VIEW_SCREEN,
                paddingBottom = DefaultValue.PADDING_VIEW_SCREEN,
                onItemClick = onItemClick
            )
        }
        item {
            ProductSectionList(
                gifts = gifts,
                title = stringResource(id = R.string.shop_section_today_offers),
                onItemClick = onItemClick
            )
        }
        item {
            ProductSectionList(
                gifts = gifts,
                title = stringResource(id = R.string.shop_section_recommended),
                onItemClick = onItemClick
            )
        }
        item {
            MarginVertical(DefaultValue.PADDING_VIEW_SCREEN)
            Row {
                Text(
                    text = stringResource(id = R.string.shop_section_for_you),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(
                            start = DefaultValue.PADDING_VIEW_SCREEN.dp,
                            end = DefaultValue.PADDING_VIEW.dp
                        )
                )
                MarginHorizontal(DefaultValue.PADDING_VIEW)
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        giftItems(
            gifts = gifts,
            onItemClick = onItemClick
        )
    }
}