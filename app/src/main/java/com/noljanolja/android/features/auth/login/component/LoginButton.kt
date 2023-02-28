package com.noljanolja.android.features.auth.login.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    isEnable: Boolean = true,
    onClick: () -> Unit,
) {
    PrimaryButton(
        text = stringResource(id = R.string.common_login),
        isEnable = isEnable,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    )
}
