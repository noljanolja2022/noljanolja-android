package com.noljanolja.android.features.home.chat.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.common.conversation.domain.model.MessageType
import com.noljanolja.android.ui.composable.BackPressHandler

private enum class InputSelector {
    NONE,
    KEYBOARD,
    EMOJI,
    EXTRA,
    CAMERA,
    GALLERY,
}

private enum class EmojiStickerSelector {
    STICKER,
    GIF
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInput(
    onMessageSent: (String, MessageType, List<Uri>) -> Unit,
    modifier: Modifier = Modifier,
    shouldShowSendButton: Boolean = false,
    resetScroll: () -> Unit = {},
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    // when gif BottomSheet is expanding, if back button is pressed, collapse bottom sheet instead of
    // close selector expand
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    val dismissKeyboard = {
        currentInputSelector = InputSelector.NONE
    }
    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackPressHandler(onBackPressed = dismissKeyboard)
        if (currentInputSelector == InputSelector.KEYBOARD) {
            LocalSoftwareKeyboardController.current?.show()
            focusRequester.requestFocus()
        }
    }

    var message by remember { mutableStateOf(TextFieldValue()) }
    var attachments by remember { mutableStateOf<List<Uri>>(listOf()) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box {
            ChatInputText(
                textField = message,
                onTextChanged = { message = it },
                focusRequester = focusRequester,
                onFocusChanged = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
                        resetScroll()
                    }
                },
                selector = currentInputSelector,
                onSelectorChanged = { currentInputSelector = it },
                onMessageSent = {
                    onMessageSent(message.text, MessageType.PlainText, listOf())
                    // Reset text field and close keyboard
                    message = TextFieldValue()
                    // Move scroll to bottom
                    resetScroll()
                    dismissKeyboard()
                },
                shouldShowSendButton = shouldShowSendButton,
            )
        }
    }
}

@Composable
private fun ChatInputText(
    textField: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    selector: InputSelector,
    onSelectorChanged: (InputSelector) -> Unit,
    onMessageSent: () -> Unit,
    shouldShowSendButton: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        var extendActionState by remember { mutableStateOf(true) }
        AnimatedVisibility(extendActionState) {
            Row(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onSelectorChanged(InputSelector.EXTRA) },
                    modifier = Modifier
                        .then(Modifier.size(36.dp))
                ) {
                    Icon(
                        if (selector == InputSelector.EXTRA) Icons.Default.More
                        else Icons.Outlined.More,
                        contentDescription = null,
                        tint = if (selector == InputSelector.EXTRA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }

                IconButton(
                    onClick = { onSelectorChanged(InputSelector.CAMERA) },
                    modifier = Modifier
                        .then(Modifier.size(36.dp))
                ) {
                    Icon(
                        Icons.Outlined.Camera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(
                    onClick = { onSelectorChanged(InputSelector.GALLERY) },
                    modifier = Modifier
                        .then(Modifier.size(36.dp))
                ) {
                    Icon(
                        if (selector == InputSelector.GALLERY) Icons.Filled.PhotoLibrary
                        else Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = if (selector == InputSelector.GALLERY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        if (!extendActionState) {
            IconButton(
                onClick = {
                    extendActionState = true
                },
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .then(Modifier.size(36.dp))
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box(
                modifier = Modifier
                    .heightIn(min = 40.dp)
                    .weight(1f)
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
            ) {
                var lastFocusState by remember { mutableStateOf(false) }
                BasicTextField(
                    value = textField,
                    onValueChange = {
                        extendActionState = false
                        onTextChanged(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .align(Alignment.CenterStart)
                        .focusRequester(focusRequester)
                        .onFocusChanged { state ->
                            if (lastFocusState != state.isFocused) {
                                onFocusChanged(state.isFocused)
                            }
                            extendActionState = !state.isFocused
                            lastFocusState = state.isFocused
                        },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                    maxLines = 4,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )
                if (textField.text.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .align(Alignment.CenterStart),
                        text = stringResource(id = R.string.chat_input_hint),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(.38F)
                        )
                    )
                }
            }
            IconButton(
                onClick = {
                    onSelectorChanged(
                        if (selector == InputSelector.EMOJI) InputSelector.KEYBOARD else InputSelector.EMOJI
                    )
                },
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .then(Modifier.size(36.dp))
            ) {
                Icon(
                    if (selector == InputSelector.EMOJI) {
                        Icons.Outlined.Keyboard
                    } else {
                        Icons.Outlined.EmojiEmotions
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            AnimatedVisibility(visible = textField.text.isNotEmpty() || shouldShowSendButton) {
                IconButton(
                    onClick = onMessageSent,
                    modifier = Modifier
                        .padding(end = 4.dp, bottom = 4.dp)
                        .then(Modifier.size(32.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(.12F),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)

                ) {
                    Icon(
                        Icons.Outlined.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatInputPreview() {
    ChatInput(
        onMessageSent = { _, _, _ -> },
    )
}