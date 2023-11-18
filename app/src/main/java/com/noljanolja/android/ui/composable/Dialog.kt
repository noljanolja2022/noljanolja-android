package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
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
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    properties: DialogProperties = DialogProperties(),
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
            containerColor = containerColor,
            titleContentColor = contentColor,
            textContentColor = contentColor,
            tonalElevation = 5.dp,
            properties = properties
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
            textContentColor = MaterialTheme.colorScheme.onBackground,
            shape = MaterialTheme.shapes.extraSmall
        )
    }
}