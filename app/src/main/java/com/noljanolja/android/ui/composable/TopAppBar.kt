package com.noljanolja.android.ui.composable

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: String = "",
    leadingTitle: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    centeredTitle: Boolean = false,
    navigationIcon: ImageVector = Icons.Default.ArrowBack,
    containerColor: Color = MaterialTheme.colorScheme.background,
    onBack: (() -> Unit)? = null,

) {
    val colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = containerColor,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    if (centeredTitle) {
        CenterAlignedTopAppBar(
            colors = colors,
            title = {
                CommonAppBarTitle(title = title, leadingTitle = leadingTitle)
            },
            actions = actions,
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            navigationIcon,
                            contentDescription = null,
                        )
                    }
                }
            },
        )
    } else {
        TopAppBar(
            colors = colors,
            title = {
                CommonAppBarTitle(title = title, leadingTitle = leadingTitle)
            },
            actions = actions,
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun CommonAppBarTitle(
    title: String,
    leadingTitle: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingTitle?.invoke()
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            ),
        )
    }
}