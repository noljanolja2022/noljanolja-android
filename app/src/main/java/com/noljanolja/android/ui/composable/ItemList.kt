package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.core.commons.*

/**
 * Created by tuyen.dang on 11/19/2023.
 */


@Composable
internal fun ItemType(
    modifier: Modifier = Modifier,
    item: ItemChoose,
    borderColor: Color = Color.Transparent,
    borderColorSelected: Color = Color.Transparent,
    textColor: Color = Color.Black,
    textColorSelected: Color = Color.Black,
    bgColor: Color = Platinum,
    bgColorSelected: Color = PrimaryGreen,
    onItemClick: (ItemChoose) -> Unit,
) {
    item.run {
        Text(
            text = name,
            style = Typography.bodySmall.copy(
                color = if (isSelected) textColorSelected else textColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .wrapContentHeight()
                .border(
                    BorderStroke(
                        width = (0.5f).dp,
                        color = if (isSelected) borderColorSelected else borderColor
                    ), shape = RoundedCornerShape(5.dp)
                )
                .clickable {
                    onItemClick(this@run)
                }
                .background(
                    color = if (isSelected) bgColorSelected else bgColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .then(modifier))
    }
}
