package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.noljanolja.android.R

@Composable
fun FullSizeUnderConstruction() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.under_construction))
        val progress by animateLottieCompositionAsState(composition)
        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            composition = composition,
            progress = { progress }
        )
    }
}