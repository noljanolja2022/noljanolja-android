package com.noljanolja.android.features.shop.search

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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.GiftItem
import com.noljanolja.android.features.shop.composable.MyCashAndVoucher
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralGrey
import com.noljanolja.android.ui.theme.shopBackground
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.shop.domain.model.Gift
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchProductScreen(
    viewModel: SearchProductViewModel = getViewModel(),
) {
    val searchKeys by viewModel.searchKeys.collectAsStateWithLifecycle()
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SearchProductContent(
        searchKeys = searchKeys,
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun SearchProductContent(
    searchKeys: List<String>,
    uiState: UiState<SearchGiftUiData>,
    handleEvent: (SearchProductEvent) -> Unit,
) {
    var isSearchFocus by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    var searchText by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.shopBackground())
    ) {
        SearchProductHeader(
            isSearchFocus = isSearchFocus,
            searchText = searchText,
            onSearchChange = {
                searchText = it
            },
            onFocusChange = {
                isSearchFocus = it
            },
            onBack = {
                handleEvent(SearchProductEvent.Back)
            },
            onSubmit = {
                handleEvent(SearchProductEvent.Search(searchText))
            }
        )
        if (isSearchFocus) {
            SearchHistory(
                searchKeys = searchKeys,
                onClear = {
                    handleEvent(SearchProductEvent.Clear(it))
                },
                onClearAll = { handleEvent(SearchProductEvent.ClearAll) },
                onSearch = {
                    handleEvent(SearchProductEvent.Search(it))
                    searchText = it
                    focusManager.clearFocus()
                }
            )
        } else {
            val data = uiState.data ?: return@Column
            SearchResult(
                myBalance = ExchangeBalance(),
                gifts = data.gifts,
                onItemClick = {
                    handleEvent(SearchProductEvent.GiftDetail(it))
                },
                onViewCoupons = {}
            )
        }
    }
}

@Composable
private fun SearchProductHeader(
    isSearchFocus: Boolean,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val background = if (isSearchFocus) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.shopBackground()
    }
    val contentColor = if (isSearchFocus) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    val searchBackground = if (isSearchFocus) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)
    }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(background)
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
                color = contentColor
            )
        }
        SizeBox(height = 5.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.clickable {
                    onBack.invoke()
                }
            )
            SizeBox(width = 15.dp)
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                searchText = searchText,
                hint = stringResource(id = R.string.shop_search_products),
                onSearch = onSearchChange,
                background = searchBackground,
                onFocusChange = {
                    onFocusChange.invoke(it.isFocused)
                },
                onSearchButton = {
                    if (searchText.isNotBlank()) {
                        onSubmit()
                        focusManager.clearFocus()
                    }
                },
                focusRequester = focusRequester
            )
        }
    }
}

@Composable
private fun SearchHistory(
    searchKeys: List<String>,
    onSearch: (String) -> Unit,
    onClear: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    if (searchKeys.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SizeBox(height = 10.dp)
        Text(
            text = stringResource(id = R.string.shop_clear_all),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClearAll.invoke() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        searchKeys.forEach {
            SizeBox(height = 10.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onSearch(it)
                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_schedule),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = NeutralGrey
                )
                SizeBox(width = 10.dp)
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Expanded()
                Icon(
                    Icons.Rounded.Cancel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClear.invoke(it) },
                    tint = NeutralGrey
                )
            }
        }
    }
}

@Composable
private fun SearchResult(
    myBalance: ExchangeBalance,
    gifts: List<Gift>,
    onViewCoupons: () -> Unit,
    onItemClick: (Gift) -> Unit,
) {
    SizeBox(height = 20.dp)
    MyCashAndVoucher(myBalance = myBalance) {
        onViewCoupons()
    }
    SizeBox(height = 20.dp)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(gifts) {
            GiftItem(
                gift = it,
                onClick = {
                    onItemClick(it)
                },
            )
        }
    }
}