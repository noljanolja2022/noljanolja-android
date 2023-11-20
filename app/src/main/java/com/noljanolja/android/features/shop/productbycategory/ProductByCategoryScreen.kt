package com.noljanolja.android.features.shop.productbycategory

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.features.home.friendoption.*
import com.noljanolja.android.features.shop.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.core.shop.domain.model.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

/**
 * Created by tuyen.dang on 11/20/2023.
 */

@Composable
fun ProductByCategoryScreen(
    categoryId: String = "",
    categoryName: String = "",
    viewModel: ProductByCategoryViewModel = getViewModel { parametersOf(categoryId) }
) {
    viewModel.run {
        val uiState by uiStateFlow.collectAsStateWithLifecycle()
        ProductByCategoryContent(
            categoryName = categoryName,
            handleEvent = ::handleEvent,
            gifts = uiState.data
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductByCategoryContent(
    categoryName: String,
    handleEvent: (ProductByCategoryEvent) -> Unit,
    gifts: MutableList<Gift>?,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        CommonTopAppBar(
            title = categoryName,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onBack = {
                handleEvent(ProductByCategoryEvent.GoBack)
            },
            centeredTitle = true
        )
        gifts?.let {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .padding(top = 12.dp)
            ) {
                items(it) {
                    GiftItem(
                        gift = it,
                        onClick = { gift ->
                            handleEvent(ProductByCategoryEvent.GiftDetail(gift.id, gift.qrCode))
                        },
                    )
                }
            }
        } ?: LoadingScreen(
            modifier = Modifier.weight(1f)
        )
    }
}
