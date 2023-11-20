package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import com.noljanolja.android.ui.theme.*

/**
 * Created by tuyen.dang on 5/7/2023.
 */

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = false) { },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = PrimaryGreen
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen(Modifier.fillMaxSize())
}