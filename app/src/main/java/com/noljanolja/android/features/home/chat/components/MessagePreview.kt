package com.noljanolja.android.features.home.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MessagePreview(
    modifier: Modifier,
    onPreviewClosed: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {},
        contentAlignment = Alignment.TopEnd,
    ) {
        IconButton(
            onClick = onPreviewClosed,
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}