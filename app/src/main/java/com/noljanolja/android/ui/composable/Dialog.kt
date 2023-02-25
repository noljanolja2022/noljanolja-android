package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.noljanolja.android.R

@Composable
fun LoadingDialog(
    title: String,
    isLoading: Boolean = false,
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                Text(title)
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ErrorDialog(
    showError: Boolean,
    title: String,
    description: String,
    onDismiss: () -> Unit
) {
    if (showError) {
        AlertDialog(
            icon = {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null
                )
            },
            title = { Text(title) },
            text = { Text(description) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            confirmButton = {},
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}