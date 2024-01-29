package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.*

/**
 * Created by tuyen.dang on 1/29/2024.
 */

@Composable
internal fun IconWithNotification(
    modifier: Modifier = Modifier,
    condition: Boolean,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) {
    if (condition) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier
                .padding(2.dp)
                .size(24.dp)
                .apply {
                    onClick?.let {
                        clickable(onClick = it)
                    }
                }
        )
    } else {
        Box(
            modifier = Modifier
                .apply {
                    onClick?.let {
                        clickable(onClick = it)
                    }
                }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier
                    .padding(2.dp)
                    .size(24.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5F)
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.error
                    )
            )
        }
    }
}
