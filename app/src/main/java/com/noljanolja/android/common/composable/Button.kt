package com.noljanolja.android.common.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R

@Composable
fun TwoButtonInRow(
    firstText: String,
    secondText: String,
    indexFocused: Int,
    modifier: Modifier = Modifier,
    fModifier: Modifier = Modifier,
    sModifier: Modifier = Modifier,
    firstClick: () -> Unit,
    secondClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(42.dp)
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        ButtonInRow(
            modifier = fModifier,
            text = firstText,
            onClick = firstClick,
            isFocused = indexFocused == 0
        )
        ButtonInRow(
            modifier = sModifier,
            text = secondText,
            onClick = secondClick,
            isFocused = indexFocused == 1
        )
    }
}

@Composable
private fun ButtonInRow(
    text: String,
    isFocused: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(
                id = if (isFocused) {
                    R.color.primary_text_color
                } else {
                    R.color.background
                }
            )
        ),
        modifier = modifier
            .height(42.dp)
    ) {
        Text(
            text = text,
            color = colorResource(id = if (isFocused) R.color.white else R.color.disable_text),
            maxLines = 1
        )
    }
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
// Preview
@Preview
@Composable
private fun TwoButtonInRowPreview() {
    TwoButtonInRow(firstText = "Login",
        secondText = "Signup",
        indexFocused = 1,
        firstClick = { }) {

    }
}