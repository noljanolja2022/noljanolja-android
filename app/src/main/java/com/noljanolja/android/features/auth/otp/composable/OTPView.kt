package com.noljanolja.android.features.auth.otp.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OTPRow(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    otp: CharArray,
    onOTPChange: (Int, Char) -> Unit,
) {
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }
    Row(modifier = modifier) {
        OTPChar(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester1,
            leftFocusRequester = null,
            rightFocusRequester = focusRequester2,
            char = otp[0],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(0, it) },
        )
        OTPChar(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester2,
            leftFocusRequester = focusRequester1,
            rightFocusRequester = focusRequester3,
            char = otp[1],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(1, it) },
        )
        OTPChar(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester3,
            leftFocusRequester = focusRequester2,
            rightFocusRequester = focusRequester4,
            char = otp[2],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(2, it) },
        )
        OTPChar(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester4,
            leftFocusRequester = focusRequester3,
            rightFocusRequester = focusRequester5,
            char = otp[3],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(3, it) },
        )
        OTPChar(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester5,
            leftFocusRequester = focusRequester4,
            rightFocusRequester = focusRequester6,
            char = otp[4],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(4, it) },
        )
        OTPChar(
            modifier = Modifier.width(24.dp),
            focusManager = focusManager,
            focusRequester = focusRequester6,
            leftFocusRequester = focusRequester5,
            rightFocusRequester = null,
            char = otp[5],
            isFillAll = otp.all { it.isDigit() },
            onCharChange = { onOTPChange(5, it) },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OTPChar(
    modifier: Modifier,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    leftFocusRequester: FocusRequester?,
    rightFocusRequester: FocusRequester?,
    char: Char,
    isFillAll: Boolean = false,
    onCharChange: (Char) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val maxChar = 1
    val code = if (char != Char.MIN_VALUE) char.toString() else ""
    val textColor =
        if (isFillAll) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicTextField(
            value = code,
            onValueChange = {
                if (it.length <= maxChar) {
                    onCharChange(
                        it.toCharArray().firstOrNull() ?: Char.MIN_VALUE
                    )
                }
                if (it.isEmpty()) {
                    leftFocusRequester?.requestFocus()
                } else {
                    rightFocusRequester?.requestFocus()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .onKeyEvent {
                    if (it.key.keyCode == Key.Backspace.keyCode && code.isEmpty()) {
                        leftFocusRequester?.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = textColor,
                textAlign = TextAlign.Center,
            ),
            cursorBrush = SolidColor(Color.Transparent),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) })
        )

        val underlineColor = when {
            isFillAll -> {
                MaterialTheme.colorScheme.secondary
            }
            !isFocused -> {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            else -> {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = underlineColor,
        )
    }
}