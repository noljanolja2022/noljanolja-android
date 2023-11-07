package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

/**
 * Created by tuyen.dang on 11/7/2023.
 */

@Composable
fun PaddingHorizontal(width: Int = 0) {
    Spacer(
        modifier = Modifier
            .width(width.dp)
            .background(Color.Transparent)
    )
}

@Composable
fun PaddingVertical(height: Int = 0) {
    Spacer(
        modifier = Modifier
            .height(height.dp)
            .background(Color.Transparent)
    )
}
