package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    title: String? = null,
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
                title?.let {
                    Text(title)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun InfoDialog(
    title: @Composable (() -> Unit)? = null,
    content: String,
    isShown: Boolean = false,
    dismissText: String,
    onDismiss: () -> Unit,
) {
    if (isShown) {
        AlertDialog(
            title = title,
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },
            confirmButton = {},
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ErrorDialog(
    showError: Boolean,
    title: String,
    description: String,
    onDismiss: () -> Unit,
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

@Composable
fun WarningDialog(
    title: String?,
    content: String,
    isWarning: Boolean = false,
    dismissText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isWarning) {
        AlertDialog(
            title = title?.let {
                { Text(text = title) }
            },
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmText, color = MaterialTheme.colorScheme.primary)
                }
            },
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground
        )
    }
}