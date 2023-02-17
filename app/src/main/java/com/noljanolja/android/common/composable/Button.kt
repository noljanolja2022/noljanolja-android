package com.noljanolja.android.common.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        ButtonInRow(
            modifier = fModifier,
            text = firstText,
            onClick = firstClick,
            isFocused = indexFocused == 0,
        )
        ButtonInRow(
            modifier = sModifier,
            text = secondText,
            onClick = secondClick,
            isFocused = indexFocused == 1,
        )
    }
}

@Composable
private fun ButtonInRow(
    text: String,
    isFocused: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.background
            },
        ),
        modifier = modifier.height(42.dp),
    ) {
        Text(
            text = text,
            color = if (isFocused) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
        )
    }
}

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: Shape? = null,
    onClick: () -> Unit,
) {
    val buttonShape = shape ?: RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        enabled = isEnable,
        colors = colors,
        modifier = modifier
            .height(50.dp)
            .shadow(2.dp, shape = buttonShape),
        shape = buttonShape,
    ) {
        Text(text)
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    shape: Shape? = null,
    containerColor: Color = MaterialTheme.colorScheme.onSecondary,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit,
) {
    val buttonShape = shape ?: RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        enabled = isEnable,
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = modifier
            .height(50.dp)
            .shadow(2.dp, shape = buttonShape),
        shape = buttonShape,
        border = BorderStroke(1.dp, contentColor),
    ) {
        Text(text)
    }
}

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape? = null,
    onClick: () -> Unit,
) {
    RoundedButton(
        modifier = modifier,
        text = text,
        isEnable = isEnable,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            contentColor = contentColor,
            disabledContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        onClick = onClick,
    )
}

// Preview
@Preview
@Composable
private fun TwoButtonInRowPreview() {
    TwoButtonInRow(firstText = "Login", secondText = "Signup", indexFocused = 1, firstClick = { }) {
    }
}
