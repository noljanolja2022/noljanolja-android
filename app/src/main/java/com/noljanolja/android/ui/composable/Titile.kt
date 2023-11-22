package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.util.*

/**
 * Created by tuyen.dang on 11/22/2023.
 */

@Composable
internal fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector?,
) {
    Row {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .wrapContentSize()
                .padding(
                    start = Constant.DefaultValue.PADDING_VIEW_SCREEN.dp,
                    end = Constant.DefaultValue.PADDING_VIEW.dp
                )
        )
        MarginHorizontal(Constant.DefaultValue.PADDING_VIEW)
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
 