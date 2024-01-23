package com.noljanolja.android.features.shop.coupons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.GiftItem
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.theme.shopBackground
import org.koin.androidx.compose.getViewModel

@Composable
fun CouponsScreen(
    viewModel: CouponsViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    CouponsContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun CouponsContent(
    uiState: UiState<CouponsUiData>,
    handleEvent: (CouponsEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.shop_coupon),
                onBack = {
                    handleEvent(CouponsEvent.Back)
                },
                centeredTitle = true,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.shopBackground())
                .padding(vertical = 10.dp),
        ) {
            items(data.myGifts) {
                GiftItem(gift = it, onClick = {
                    handleEvent(CouponsEvent.GiftDetail(it.giftId(), it.qrCode, it.log))
                })
            }
        }
//        LazyVerticalGrid(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(vertical = 10.dp, horizontal = 16.dp),
//            columns = GridCells.Fixed(2),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//        ) {
//            items(data.myGifts) {
//                CouponItem(
//                    gift = it,
//                    onUse = {
//                        handleEvent(CouponsEvent.GiftDetail(it.giftId(), it.qrCode))
//                    }
//                )
//            }
//        }
    }
}