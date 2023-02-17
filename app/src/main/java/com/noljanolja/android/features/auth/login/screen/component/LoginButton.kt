package com.noljanolja.android.features.auth.login.screen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.PrimaryButton

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = stringResource(id = R.string.login),
        isEnable = isEnable,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    )
}
