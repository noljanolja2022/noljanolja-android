package com.noljanolja.android.features.shop.main

import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.modifier.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.features.shop.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.Constant.*
import com.noljanolja.core.commons.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.shop.domain.model.*
import org.koin.androidx.compose.*

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
            data.run {
                ProductsAndVouchers(
                    gifts = gifts,
                    topFeatureGifts = topFeatureGifts,
                    myGifts = myGifts,
                    todayOfferGifts = todayOfferGift,
                    recommendsGift = recommendsGift,
                    brands = brands,
                    onBrandItemClick = {
                        handleEvent(
                            ShopEvent.ViewGiftType(
                                brandId = it.id,
                                categoryName = it.name
                            )
                        )
                    },
                    onGiftItemClick = {
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
    topFeatureGifts: List<Gift>,
    todayOfferGifts: List<Gift>,
    recommendsGift: List<Gift>,
    onGiftItemClick: (Gift) -> Unit,
    brands: List<ItemChoose>,
    onBrandItemClick: (ItemChoose) -> Unit,
    myGifts: List<Gift>,
    onUse: (Gift) -> Unit,
) {
    if (gifts.isEmpty()
        && topFeatureGifts.isEmpty()
    ) {
        Box {
            Text(
                text = stringResource(id = R.string.shop_all_sold_out),
                color = textColor()
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (topFeatureGifts.isNotEmpty()) {
                ProductSectionList(
                    gifts = topFeatureGifts,
                    title = stringResource(id = R.string.shop_section_top_feature),
                    containerColor = PrimaryGreen,
                    titleColor = Color.Black,
                    paddingTop = DefaultValue.PADDING_VIEW_SCREEN,
                    paddingBottom = DefaultValue.PADDING_VIEW_SCREEN,
                    onItemClick = onGiftItemClick
                )
            }
            if (brands.isNotEmpty()) {
                HorizontalSectionList(
                    items = brands,
                    title = stringResource(id = R.string.shop_brands),
                    titleColor = Color.Black,
                    containerColor = Orange00,
                    paddingTop = 5,
                    paddingBottom = 5,
                    itemContent = { brand ->
                        BrandItem(
                            brand = brand,
                            onItemClick = onBrandItemClick
                        )
                    }
                )
            }
            if (todayOfferGifts.isNotEmpty()) {
                ProductSectionList(
                    gifts = todayOfferGifts,
                    title = stringResource(id = R.string.shop_section_today_offers),
                    onItemClick = onGiftItemClick
                )
            }
            if (recommendsGift.isNotEmpty()) {
                ProductSectionList(
                    gifts = recommendsGift,
                    title = stringResource(id = R.string.shop_section_recommended),
                    onItemClick = onGiftItemClick
                )
            }
            if (gifts.isNotEmpty()) {
                MarginVertical(DefaultValue.PADDING_VIEW_SCREEN)
                SectionTitle(
                    title = stringResource(id = R.string.shop_section_for_you),
                    icon = Icons.Default.NavigateNext
                )
                gifts.forEach {
                    GiftItem(
                        gift = it,
                        onClick = onGiftItemClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductSectionList(
    modifier: Modifier = Modifier,
    title: String,
    gifts: List<Gift>,
    containerColor: Color = Color.Transparent,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    paddingTop: Int = DefaultValue.PADDING_HORIZONTAL_SCREEN,
    paddingBottom: Int = 0,
    onItemClick: (Gift) -> Unit
) {
    HorizontalSectionList(
        modifier = modifier,
        items = gifts,
        title = title,
        containerColor = containerColor,
        titleColor = titleColor,
        paddingTop = paddingTop,
        paddingBottom = paddingBottom,
        itemContent = { gift ->
            ProductSection(
                gift = gift,
                onItemClick = onItemClick
            )
        }
    )
}
