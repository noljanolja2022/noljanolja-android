package com.noljanolja.android.features.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.UserPoint
import com.noljanolja.android.ui.theme.helpIconColor
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.formatDigitsNumber

@Composable
fun ShopScreen() {
    ShopContent()
}

@Composable
private fun ShopContent() {
    ScaffoldWithUiState(
        uiState = UiState<Any>()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            SearchProductHeader()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SizeBox(height = 10.dp)
                    UserPoint(
                        point = 100000.formatDigitsNumber(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SizeBox(height = 16.dp)
                }
                item {
                    ExchangeCoupons()
                }
            }
        }
    }
}

@Composable
private fun SearchProductHeader() {
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
                text = "Welcome to Nolja shop!",
                style = MaterialTheme.typography.titleSmall.withMedium()
            )
            Icon(
                Icons.Default.Help,
                contentDescription = null,
                tint = MaterialTheme.helpIconColor(),
                modifier = Modifier.size(16.dp)
            )
        }
        SizeBox(height = 5.dp)
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { println("111111") },
            searchText = "",
            hint = "Search products",
            onSearch = {},
            enabled = false,
        )
    }
}

@Composable
private fun ExchangeCoupons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Exchanged Coupons",
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Expanded()
        Text(
            text = "View all",
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = Color.White
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}