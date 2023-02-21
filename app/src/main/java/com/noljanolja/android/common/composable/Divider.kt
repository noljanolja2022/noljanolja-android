package com.noljanolja.android.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEEEEEE)),
    )
}
