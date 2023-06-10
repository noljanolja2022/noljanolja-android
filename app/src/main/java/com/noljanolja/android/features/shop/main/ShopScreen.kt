package com.noljanolja.android.features.shop.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.CouponItem
import com.noljanolja.android.features.shop.composable.HelpDialog
import com.noljanolja.android.features.shop.composable.ProductItem
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.UserPoint
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.formatDigitsNumber
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

@Composable
private fun ShopContent(
    uiState: UiState<ShopUiData>,
    handleEvent: (ShopEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState
    ) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            SearchProductHeader(
                goToSearch = { handleEvent(ShopEvent.Search) },
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SizeBox(height = 10.dp)
                    UserPoint(
                        point = data.memberInfo.point.formatDigitsNumber(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SizeBox(height = 16.dp)
                }
                item {
                    ExchangeCoupons(
                        coupons = data.myGifts,
                        onItemClick = { handleEvent(ShopEvent.GiftDetail(it.id)) },
                        viewAllCoupons = {
                            handleEvent(ShopEvent.ViewAllCoupons)
                        }
                    )
                }
                shop(
                    gifts = data.gifts,
                    onItemClick = {
                        handleEvent(ShopEvent.GiftDetail(it.id))
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
                style = MaterialTheme.typography.titleSmall.withMedium()
            )
            Icon(
                Icons.Default.Help,
                contentDescription = null,
                tint = if (isShowHelp) {
                    MaterialTheme.colorScheme.primary
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
        SizeBox(height = 5.dp)
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

@Composable
private fun ExchangeCoupons(
    coupons: List<Gift>,
    onItemClick: (Gift) -> Unit,
    viewAllCoupons: () -> Unit,
) {
    if (coupons.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.shop_exchanged_coupons),
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Expanded()
        Text(
            text = stringResource(id = R.string.shop_view_all),
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = Color.White,
            modifier = Modifier.clickable {
                viewAllCoupons.invoke()
            }
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    viewAllCoupons.invoke()
                }
        )
    }
    SizeBox(height = 10.dp)
    LazyRow(modifier = Modifier.padding(start = 16.dp)) {
        items(coupons.size) { index ->
            CouponItem(
                gift = coupons[index],
                modifier = Modifier.width(130.dp),
                onUse = { onItemClick.invoke(coupons[index]) }
            )
            SizeBox(width = 12.dp)
        }
    }
    SizeBox(height = 30.dp)
}

fun LazyListScope.shop(
    gifts: List<Gift>,
    onItemClick: (Gift) -> Unit,
) {
    item {
        Text(
            text = stringResource(id = R.string.common_shop),
            style = MaterialTheme.typography.bodyLarge.withBold(),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        SizeBox(height = 10.dp)
    }
    items(count = (gifts.size + 1) / 2) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            gifts[row * 2].let {
                ProductItem(
                    gift = it,
                    modifier = Modifier
                        .weight(1F),
                    onClick = {
                        onItemClick.invoke(it)
                    }
                )
            }

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
        SizeBox(height = 20.dp)
    }
}