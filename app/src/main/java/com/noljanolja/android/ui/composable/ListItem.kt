package com.noljanolja.android.ui.composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import com.noljanolja.android.features.common.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_HORIZONTAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.TWEEN_ANIMATION_TIME
import com.noljanolja.core.commons.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.*

/**
 * Created by tuyen.dang on 11/20/2023.
 */

@Composable
internal fun ContactList(
    modifier: Modifier = Modifier,
    contacts: List<ShareContact>,
    scrollState: LazyListState,
    selectedContacts: List<ShareContact>,
    onItemClick: (ShareContact) -> Unit,
    loadMoreContacts: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState
    ) {
        items(contacts) { contact ->
            ContactRow(
                contact = contact,
                selected = selectedContacts.contains(contact)
            ) { onItemClick(it) }
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = loadMoreContacts)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ListTypes(
    modifier: Modifier = Modifier,
    types: MutableList<ItemChoose>,
    onItemClick: (ItemChoose) -> Unit,
    paddingHorizontal: Int = PADDING_HORIZONTAL_SCREEN
) {
    val listTypeState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier),
        state = listTypeState,
        horizontalArrangement = Arrangement.spacedBy(PADDING_VIEW.dp),
        contentPadding = PaddingValues(horizontal = paddingHorizontal.dp)
    ) {
        items(
            items = types,
            key = { typeKey ->
                typeKey.toString()
            },
        ) { type ->
            ItemType(
                item = type,
                onItemClick = {
//                    if (types.indexOf(it) != -1) {
//                        val newValue = it.copy(isSelected = !it.isSelected)
//                        types[types.indexOf(it)] = newValue
//                        types.run {
//                            sortBy { item ->
//                                item.id
//                            }
//                            sortByDescending { item ->
//                                item.isSelected
//                            }
//                            coroutineScope.launch {
//                                delay(TWEEN_ANIMATION_TIME.toLong())
//                                listTypeState.animateScrollToItem(0)
//                                onItemClick()
//                            }
//                        }
//                    }
                    onItemClick(it)
                },
                modifier = Modifier.animateItemPlacement(
                    tween(durationMillis = TWEEN_ANIMATION_TIME)
                )
            )
        }
    }
}

@Composable
internal fun <T>HorizontalSectionList(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T>,
    containerColor: Color = Color.Transparent,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    paddingTop: Int = PADDING_HORIZONTAL_SCREEN,
    paddingBottom: Int = 0,
    itemContent: @Composable (T) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .background(containerColor)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        val (tvTitle, btnSeeAll, listData, marginBottom) = createRefs()
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = titleColor,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = PADDING_VIEW_SCREEN.dp, end = PADDING_VIEW.dp)
                .constrainAs(tvTitle) {
                    top.linkTo(parent.top, paddingTop.dp)
                    linkTo(
                        start = parent.start,
                        end = btnSeeAll.start,
                        bias = 0f
                    )
                }
        )
        Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(btnSeeAll) {
                    linkTo(tvTitle.top, tvTitle.bottom)
                    linkTo(
                        start = tvTitle.end,
                        end = parent.end,
                        bias = 0f
                    )
                }
        )
        createHorizontalChain(tvTitle, btnSeeAll, chainStyle = ChainStyle.Packed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(listData) {
                    top.linkTo(tvTitle.bottom, PADDING_VIEW.dp)
                }
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = PADDING_VIEW_SCREEN.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                itemContent(item)
            }
        }
        Spacer(
            modifier = Modifier
                .height(paddingBottom.dp)
                .constrainAs(marginBottom) {
                    top.linkTo(listData.bottom)
                }
        )
    }
}
