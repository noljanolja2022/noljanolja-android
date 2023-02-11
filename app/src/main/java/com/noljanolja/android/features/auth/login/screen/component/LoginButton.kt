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

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        enabled = isEnable,
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(2.dp, shape = shape),
        shape = shape
    ) {
        Text(text)
    }
}