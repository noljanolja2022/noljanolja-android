package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SizeBox(width: Dp = 0.dp, height: Dp = 0.dp) {
    Spacer(modifier = Modifier.width(width).height(height))
}

@Composable
fun ColumnScope.Expanded() = Spacer(modifier = Modifier.weight(1F))

@Composable
fun RowScope.Expanded() = Spacer(modifier = Modifier.weight(1F))