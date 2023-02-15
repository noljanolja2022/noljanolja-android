package com.noljanolja.android.features.auth.login.screen.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.RoundedButton

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {
    RoundedButton(
        modifier = modifier,
        text = stringResource(id = R.string.login),
        isEnable = isEnable,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            contentColor = Color.White,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        ),
        onClick = onClick
    )
}
