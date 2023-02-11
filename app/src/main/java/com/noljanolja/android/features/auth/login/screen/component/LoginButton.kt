package com.noljanolja.android.features.auth.login.screen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
            containerColor = colorResource(id = R.color.primaryColor),
            disabledContainerColor = colorResource(id = R.color.background),
            contentColor = Color.White,
            disabledContentColor = colorResource(id = R.color.disable_text)
        ),
        onClick = onClick
    )
}