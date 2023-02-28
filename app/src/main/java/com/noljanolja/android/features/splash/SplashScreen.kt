package com.noljanolja.android.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
        )
        if (!uiState.loading) {
            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth(),
                text = "Continue",
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                viewModel.handleEvent(SplashEvent.Continue)
            }
        }
    }
}
