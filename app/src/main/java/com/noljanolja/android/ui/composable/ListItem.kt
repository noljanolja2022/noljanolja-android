package com.noljanolja.android.ui.composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_HORIZONTAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW
import com.noljanolja.android.util.Constant.DefaultValue.TWEEN_ANIMATION_TIME
import com.noljanolja.core.commons.*
import kotlinx.coroutines.*

/**
 * Created by tuyen.dang on 11/20/2023.
 */

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
