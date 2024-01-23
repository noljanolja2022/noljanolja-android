package com.noljanolja.android.ui.composable

import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
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
    modifier: Modifier = Modifier,
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
            modifier = modifier,
            title = title?.let {
                {
                    Text(
                        text = title,
                        fontSize = 20.sp
                    )
                }
            },
            text = {
                Text(
                    text = content,
                    fontSize = 16.sp
                )
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = dismissText,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        text = confirmText,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
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

@Composable
internal fun DialogWarningWithPicture(
    @DrawableRes image: Int,
    title: String? = null,
    message: String? = null,
    dismissText: String? = null,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 13.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            MarginVertical(24)
            title?.let {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = it,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                MarginVertical(5)
            }
            message?.let {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                MarginVertical(22)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                dismissText?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onDismiss()
                        }
                    )
                }
                MarginHorizontal(12)
                Text(
                    text = confirmText,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onConfirm()
                    }
                )
            }
        }
    }
}

@Composable
internal fun DialogWarningWithContent(
    dismissText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(
                    horizontal = 13.dp
                )
                .padding(
                    top = 16.dp,
                    bottom = 35.dp
                )
        ) {
            content()
            MarginVertical(18)
            Row {
                OutlineButtonBorderRadius(
                    title = dismissText,
                    borderColor = MaterialTheme.colorScheme.primary,
                    bgColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                MarginHorizontal(12)
                ButtonRadius(
                    title = confirmText,
                    bgColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.Black,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
