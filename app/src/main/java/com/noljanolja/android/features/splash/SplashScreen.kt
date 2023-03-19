package com.noljanolja.android.features.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithLogo
import org.koin.androidx.compose.getViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ScaffoldWithLogo(
        modifier = Modifier.paint(
            painter = painterResource(id = R.drawable.bg_splash),
            contentScale = ContentScale.FillWidth
        )
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            stringResource(id = R.string.welcome_noljanolja),
            modifier = Modifier.padding(horizontal = 47.dp),
            style = with(MaterialTheme) {
                typography.bodyLarge.copy(colorScheme.onPrimary)
            },
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(46.dp))
        if (!uiState.loading) {
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.splash_explore),
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                viewModel.handleEvent(SplashEvent.Continue)
            }
        } else {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
