package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.Constant.DefaultValue.BUTTON_HEIGHT
import com.noljanolja.android.util.Constant.DefaultValue.BUTTON_TITLE
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_ICON

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onClick: () -> Unit,
) {
    val buttonShape = shape ?: RoundedCornerShape(8.dp)
    Card(
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .height(50.dp),
        shape = buttonShape,
        enabled = isEnable,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, style = style)
        }
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
    disabledContainerColor: Color = MaterialTheme.colorScheme.surface,
    disabledContentColor: Color = MaterialTheme.colorScheme.outline,
    shape: Shape? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    style: TextStyle = MaterialTheme.typography.bodyMedium.withMedium(),
    onClick: () -> Unit,
) {
    RoundedButton(
        modifier = modifier,
        text = text,
        isEnable = isEnable,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        ),
        onClick = onClick,
        style = style,
        contentPadding = contentPadding
    )
}

@Composable
fun AppIconButton(
    modifier: Modifier,
    size: Int = 24,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    icon: ImageVector?,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        icon?.let {
            Icon(
                it,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}

@Composable
internal fun ButtonRadius(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Int = 5,
    title: String,
    textSize: Int = 14,
    height: Int = BUTTON_HEIGHT,
    bgColor: Color,
    icon: Painter? = null,
    textColor: Color = Color.White,
    bgDisableColor: Color = NeutralDeepGrey,
    onClick: () -> Unit
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .height(height.dp)
            .then(modifier),
        shape = RoundedCornerShape(radius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            disabledContainerColor = bgDisableColor
        ),
        elevation = null,
    ) {
        icon?.let {
            Icon(
                it,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(textSize.dp)
            )
            MarginHorizontal(5)
        }
        Text(
            text = title,
            style = Typography.bodyMedium.copy(
                color = textColor,
                fontSize = textSize.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun ButtonTextWithToggle(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Int = 5,
    title: String,
    textSize: Int = 14,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    height: Int = BUTTON_TITLE,
    bgColor: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    bgDisableColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    ElevatedButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .height(height.dp)
            .then(modifier),
        shape = RoundedCornerShape(radius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            disabledContainerColor = bgDisableColor
        )
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = PADDING_ICON.dp),
            text = title,
            style = textStyle.copy(
                color = textColor,
                fontSize = textSize.sp,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        checked?.let {
            Switch(
                modifier = Modifier
                    .height(30.dp)
                    .width(50.dp),
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = Color.Transparent,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkedThumbColor = MaterialTheme.colorScheme.background
                ),
            )
        }
    }
}

// Preview
@Preview
@Composable
private fun TwoButtonInRowPreview() {
    TwoButtonInRow(firstText = "Login", secondText = "Signup", indexFocused = 1, firstClick = { }) {
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    PrimaryButton(
        text = "Test",
        onClick = {}
    )
}

@Preview
@Composable
private fun ButtonRadiusPreview() {
    ButtonRadius(
        title = "Test",
        bgColor = PictonBlue,
        textColor = Color.Black,
        icon = painterResource(id = R.drawable.ic_chat),
        onClick = {}
    )
}

@Preview
@Composable
private fun ButtonTextPreview() {
    ButtonTextWithToggle(
        title = "Test",
        bgColor = PictonBlue,
        textColor = Color.Black,
        onClick = {}
    )
}
