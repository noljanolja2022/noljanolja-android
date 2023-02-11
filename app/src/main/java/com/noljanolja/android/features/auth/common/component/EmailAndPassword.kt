package com.noljanolja.android.features.auth.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R

@Composable
fun EmailAndPassword(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        RoundedTextField(
            value = email,
            hint = stringResource(id = R.string.email_hint_text),
            onValueChange = onEmailChange,
        )
        Spacer(modifier = Modifier.height(12.dp))
        RoundedTextField(
            value = password,
            hint = stringResource(id = R.string.password_hint_text),
            isPassword = true,
            onValueChange = onPasswordChange,
        )
    }
}

@Composable
fun RoundedTextField(
    value: String,
    hint: String? = null,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    TextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.run {
            textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                backgroundColor = colorResource(id = R.color.background),
                unfocusedIndicatorColor = Color.Transparent,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Email else KeyboardType.Password
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
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}
