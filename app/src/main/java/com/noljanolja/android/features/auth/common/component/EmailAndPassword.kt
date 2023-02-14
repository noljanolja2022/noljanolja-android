package com.noljanolja.android.features.auth.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.util.getErrorMessage

@Composable
fun EmailAndPassword(
    email: String,
    password: String,
    emailError: Throwable?,
    passwordError: Throwable?,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        RoundedTextField(
            value = email,
            error = emailError,
            hint = stringResource(id = R.string.email_hint_text),
            onValueChange = onEmailChange,
        )
        emailError?.let {
            Text(
                context.getErrorMessage(it),
                modifier = Modifier
                    .padding(start = 24.dp, top = 12.dp)
                    .align(Alignment.Start),
                style = TextStyle(
                    color = colorResource(id = R.color.error_text_color), fontSize = 14.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        RoundedTextField(
            value = password,
            hint = stringResource(id = R.string.password_hint_text),
            error = passwordError,
            hideText = true,
            onValueChange = onPasswordChange,
        )
        passwordError?.let {
            Text(
                context.getErrorMessage(it),
                modifier = Modifier
                    .padding(start = 24.dp, top = 12.dp)
                    .align(Alignment.Start),
                style = TextStyle(
                    color = colorResource(id = R.color.error_text_color),
                    fontSize = 14.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedTextField(
    value: String,
    hint: String? = null,
    error: Throwable? = null,
    hideText: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor =
        error?.let { colorResource(id = R.color.error_text_color) } ?: Color.Transparent
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = borderColor,
            containerColor = colorResource(id = R.color.background),
            unfocusedIndicatorColor = borderColor,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (hideText) KeyboardType.Email else KeyboardType.Password
        ),
        shape = shape,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = shape
            ),
        placeholder = hint?.let {
            {
                Text(text = hint, color = colorResource(id = R.color.disable_text))
            }
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = colorResource(id = R.color.primary_text_color)
        ),
        visualTransformation = if (hideText) PasswordVisualTransformation() else VisualTransformation.None,
    )
}
