package com.noljanolja.android.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Green100
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.darkContent
import org.koin.androidx.compose.getViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Green100)
                .padding(paddingValues)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .width(188.dp)
                    .height(176.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                stringResource(id = R.string.welcome_noljanolja),
                modifier = Modifier.padding(horizontal = 47.dp),
                style = with(MaterialTheme) {
                    typography.titleMedium.copy(colorScheme.onPrimary)
                },
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(46.dp))
            if (!uiState.loading) {
                PrimaryButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = if (uiState.needReload) R.string.common_reload else R.string.splash_explore),
                    containerColor = NeutralLight,
                    contentColor = MaterialTheme.darkContent()
                ) {
                    viewModel.handleEvent(SplashEvent.Continue)
                }
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 63.dp)
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.onBackground,
                    trackColor = MaterialTheme.colorScheme.background,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    stringResource(id = R.string.splash_wait),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.darkContent()
                )
            }
            SizeBox(height = 14.dp)
            Image(painter = painterResource(id = R.drawable.ic_moneys), contentDescription = null)
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
