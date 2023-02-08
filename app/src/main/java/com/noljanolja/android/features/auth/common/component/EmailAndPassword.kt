package com.noljanolja.android.features.auth.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EmailAndPassword(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        TextField(value = email, onValueChange = onEmailChange)
        TextField(value = password, onValueChange = onPasswordChange)
    }
}
