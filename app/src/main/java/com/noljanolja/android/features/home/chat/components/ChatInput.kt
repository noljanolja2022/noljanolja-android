//package com.noljanolja.android.features.home.chat.components
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.Divider
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.focus.focusTarget
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//private enum class InputSelector {
//    NONE,
//    KEYBOARD,
//    EMOJI,
//    EXTRA,
//    CAMERA,
//    GALLERY,
//}
//
//private enum class EmojiStickerSelector {
//    STICKER,
//    GIF
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun ChatInput(
//    onMessageSent: (String, MessageType, List<Uri>) -> Unit,
//    onMessagePreview: (Any, MessageType) -> Unit,
//    modifier: Modifier = Modifier,
//    shouldShowSendButton: Boolean = false,
//    resetScroll: () -> Unit = {},
//    mediaList: List<Pair<Uri, Long?>>,
//    loadMedia: () -> Unit,
//    openPhoneSetting: () -> Unit,
//    onGifTabSelected: (Boolean) -> Unit,
//    onHandleBottomSheetBackPress: () -> Unit = {},
//    isGifsListExpanding: Boolean = false,
//) {
//    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
//    // when gif BottomSheet is expanding, if back button is pressed, collapse bottom sheet instead of
//    // close selector expand
//    val dismissKeyboard = {
//        if (!isGifsListExpanding) {
//            currentInputSelector = InputSelector.NONE
//        }
//
//        onHandleBottomSheetBackPress()
//    }
//    val focusRequester = remember { FocusRequester() }
//    val context = LocalContext.current
//
//    // Intercept back navigation if there's a InputSelector visible
//    if (currentInputSelector != InputSelector.NONE) {
//        BackPressHandler(onBackPressed = dismissKeyboard)
//        if (currentInputSelector == InputSelector.KEYBOARD) {
//            LocalSoftwareKeyboardController.current?.show()
//            focusRequester.requestFocus()
//        }
//    }
//
//    var message by remember { mutableStateOf(TextFieldValue()) }
//    var attachments by remember { mutableStateOf<List<Uri>>(listOf()) }
//    var showConfirmDialog by remember { mutableStateOf(false) }
//    val selectedMedia = remember { mutableStateListOf<Uri>() }
//
//    Column(modifier = modifier) {
//        Box {
//            if (selectedMedia.isEmpty()) {
//                ChatInputText(
//                    textField = message,
//                    onTextChanged = { message = it },
//                    focusRequester = focusRequester,
//                    onFocusChanged = { focused ->
//                        if (focused) {
//                            currentInputSelector = InputSelector.NONE
//                            onHandleBottomSheetBackPress()
//                            resetScroll()
//                        }
//                    },
//                    selector = currentInputSelector,
//                    onSelectorChanged = { currentInputSelector = it },
//                    onMessageSent = {
//                        onMessageSent(message.text, MessageType.PlainText, listOf())
//                        // Reset text field and close keyboard
//                        message = TextFieldValue()
//                        // Move scroll to bottom
//                        resetScroll()
//                        dismissKeyboard()
//                    },
//                    shouldShowSendButton = shouldShowSendButton,
//                )
//            } else {
//                SendMedia(
//                    numberSelected = selectedMedia.size,
//                    onSendMedia = {
//                        onMessageSent(
//                            message.text,
//                            MessageType.Photo,
//                            selectedMedia
//                        )
//                        selectedMedia.clear()
//                    }
//                )
//            }
//        }
//
//        SelectorExpanded(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(288.dp),
//            currentSelector = currentInputSelector,
//            onStickerClicked = { onMessagePreview(it, MessageType.Sticker) },
//            onMediaSelect = { mediaSelect, isAdd ->
//                when (isAdd) {
//                    true -> selectedMedia.addAll(mediaSelect)
//                    false -> selectedMedia.removeAll(mediaSelect)
//                    else -> {
//                        mediaSelect.forEach { media ->
//                            if (!selectedMedia.contains(media)) {
//                                selectedMedia.add(media)
//                            }
//                        }
//                    }
//                }
//            },
//            onCall = {},
//            onDocumentSelected = {
//                attachments = it
//                showConfirmDialog = true
//            },
//            mediaList = mediaList,
//            selectedMedia = selectedMedia,
//            loadMedia = loadMedia,
//            openPhoneSetting = openPhoneSetting,
//            onGifTabSelected = onGifTabSelected,
//            onHideGifTab = onHandleBottomSheetBackPress
//        )
//    }
//
//    if (showConfirmDialog) {
//        val fileName = context.getName(attachments.first())
//
//        AlertDialog(
//            text = { Text(stringResource(R.string.chat_send_file_confirmation, fileName)) },
//            dismissButton = {
//                TextButton(onClick = {
//                    showConfirmDialog = false
//                    attachments = listOf()
//                }) {
//                    Text(stringResource(R.string.common_cancel))
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    onMessageSent(message.text, MessageType.Document, attachments)
//                    showConfirmDialog = false
//                    attachments = listOf()
//                }) {
//                    Text(stringResource(R.string.common_confirm))
//                }
//            },
//            onDismissRequest = {
//                showConfirmDialog = false
//                attachments = listOf()
//            },
//        )
//    }
//
//    if (currentInputSelector == InputSelector.CAMERA) {
//        onHandleBottomSheetBackPress()
//
//        val selectedFile = remember { mutableStateOf<Uri?>(null) }
//        val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
//            currentInputSelector = InputSelector.NONE
//            if (it) {
//                selectedFile.value?.let {
//                    onMessageSent(message.text, MessageType.Photo, listOf(it))
//                }
//                selectedFile.value = null
//            }
//        }
//        val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) {
//            currentInputSelector = InputSelector.NONE
//            if (it) {
//                selectedFile.value?.let {
//                    onMessageSent(message.text, MessageType.Photo, listOf(it))
//                }
//                selectedFile.value = null
//            }
//        }
//
//        CameraSelector(
//            onTakePhoto = {
//                selectedFile.value = context.getTmpFileUri("temp_photo", ".png").also {
//                    photoLauncher.launch(it)
//                }
//            },
//            onTakeVideo = {
//                selectedFile.value = context.getTmpFileUri("temp_video", ".mp4").also {
//                    videoLauncher.launch(it)
//                }
//            },
//            onDismiss = { currentInputSelector = InputSelector.NONE },
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun PreviewSendMedia() {
//    SendMedia(numberSelected = 2, onSendMedia = {})
//}
//
//@Composable
//private fun SendMedia(
//    numberSelected: Int,
//    onSendMedia: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .height(48.dp)
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.onPrimary),
//        contentAlignment = Alignment.CenterEnd,
//    ) {
//        Text(
//            stringResource(id = R.string.args_chat_selected, numberSelected),
//            style = MaterialTheme.typography.titleSmall,
//            color = MaterialTheme.colorScheme.onSurface.copy(.87F),
//            modifier = Modifier
//                .align(Alignment.Center)
//
//        )
//        IconButton(
//            onClick = onSendMedia,
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = 12.dp)
//                .then(Modifier.size(32.dp))
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(.12F),
//                    shape = RoundedCornerShape(12.dp)
//                )
//                .background(
//                    color = MaterialTheme.colorScheme.primary,
//                    shape = RoundedCornerShape(12.dp)
//                )
//                .padding(4.dp)
//
//        ) {
//            Icon(
//                painterResource(R.drawable.ic_send_line),
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onPrimary
//            )
//        }
//    }
//}
//
//@Composable
//private fun ChatInputText(
//    textField: TextFieldValue,
//    onTextChanged: (TextFieldValue) -> Unit,
//    focusRequester: FocusRequester,
//    onFocusChanged: (Boolean) -> Unit,
//    selector: InputSelector,
//    onSelectorChanged: (InputSelector) -> Unit,
//    onMessageSent: () -> Unit,
//    shouldShowSendButton: Boolean = false
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .background(MaterialTheme.colorScheme.onPrimary)
//            .padding(4.dp),
//        horizontalArrangement = Arrangement.End,
//        verticalAlignment = Alignment.Bottom,
//    ) {
//        var extendActionState by remember { mutableStateOf(true) }
//        AnimatedVisibility(extendActionState) {
//            Row(
//                modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                IconButton(
//                    onClick = { onSelectorChanged(InputSelector.EXTRA) },
//                    modifier = Modifier
//                        .then(Modifier.size(36.dp))
//                ) {
//                    Icon(
//                        painterResource(
//                            if (selector == InputSelector.EXTRA) R.drawable.ic_chat_extra_fill
//                            else R.drawable.ic_chat_extra_line
//                        ),
//                        contentDescription = null,
//                        tint = if (selector == InputSelector.EXTRA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
//                    )
//                }
//
//                IconButton(
//                    onClick = { onSelectorChanged(InputSelector.CAMERA) },
//                    modifier = Modifier
//                        .then(Modifier.size(36.dp))
//                ) {
//                    Icon(
//                        painterResource(R.drawable.ic_photo_camera_line),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onSurface,
//                    )
//                }
//                IconButton(
//                    onClick = { onSelectorChanged(InputSelector.GALLERY) },
//                    modifier = Modifier
//                        .then(Modifier.size(36.dp))
//                ) {
//                    Icon(
//                        painterResource(
//                            if (selector == InputSelector.GALLERY) R.drawable.ic_chat_photo_library_fill
//                            else R.drawable.ic_chat_photo_library_line
//                        ),
//                        contentDescription = null,
//                        tint = if (selector == InputSelector.GALLERY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
//                    )
//                }
//            }
//        }
//        if (!extendActionState) {
//            IconButton(
//                onClick = {
//                    extendActionState = true
//                },
//                modifier = Modifier
//                    .padding(bottom = 2.dp)
//                    .then(Modifier.size(36.dp))
//            ) {
//                Icon(
//                    painterResource(R.drawable.ic_chevron_right),
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.onSurface,
//                )
//            }
//        }
//        Row(
//            modifier = Modifier
//                .weight(1f)
//                .clip(RoundedCornerShape(16.dp))
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.surfaceVariant,
//                    shape = RoundedCornerShape(16.dp)
//                )
//                .background(MaterialTheme.colorScheme.surface),
//            verticalAlignment = Alignment.Bottom,
//        ) {
//            Box(
//                modifier = Modifier
//                    .heightIn(min = 40.dp)
//                    .weight(1f)
//                    .wrapContentHeight()
//                    .align(Alignment.CenterVertically)
//            ) {
//                var lastFocusState by remember { mutableStateOf(false) }
//                BasicTextField(
//                    value = textField,
//                    onValueChange = {
//                        extendActionState = false
//                        onTextChanged(it)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp, horizontal = 12.dp)
//                        .align(Alignment.CenterStart)
//                        .focusRequester(focusRequester)
//                        .onFocusChanged { state ->
//                            if (lastFocusState != state.isFocused) {
//                                onFocusChanged(state.isFocused)
//                            }
//                            extendActionState = !state.isFocused
//                            lastFocusState = state.isFocused
//                        },
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
//                    maxLines = 4,
//                    cursorBrush = SolidColor(LocalContentColor.current),
//                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
//                )
//                if (textField.text.isEmpty()) {
//                    Text(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp, horizontal = 12.dp)
//                            .align(Alignment.CenterStart),
//                        text = stringResource(id = R.string.chat_input_hint),
//                        style = MaterialTheme.typography.bodyLarge.copy(
//                            color = MaterialTheme.colorScheme.onSurface.copy(.38F)
//                        )
//                    )
//                }
//            }
//            IconButton(
//                onClick = {
//                    onSelectorChanged(
//                        if (selector == InputSelector.EMOJI) InputSelector.KEYBOARD else InputSelector.EMOJI
//                    )
//                },
//                modifier = Modifier
//                    .padding(bottom = 2.dp)
//                    .then(Modifier.size(36.dp))
//            ) {
//                Icon(
//                    if (selector == InputSelector.EMOJI)
//                        painterResource(R.drawable.ic_keyboard_line)
//                    else
//                        painterResource(R.drawable.ic_emoticon_line),
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.onSurface,
//                )
//            }
//            AnimatedVisibility(visible = textField.text.isNotEmpty() || shouldShowSendButton) {
//                IconButton(
//                    onClick = onMessageSent,
//                    modifier = Modifier
//                        .padding(end = 4.dp, bottom = 4.dp)
//                        .then(Modifier.size(32.dp))
//                        .border(
//                            width = 1.dp,
//                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(.12F),
//                            shape = RoundedCornerShape(12.dp)
//                        )
//                        .background(
//                            color = MaterialTheme.colorScheme.primary,
//                            shape = RoundedCornerShape(12.dp)
//                        )
//                        .padding(4.dp)
//
//                ) {
//                    Icon(
//                        painterResource(R.drawable.ic_send_line),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SelectorExpanded(
//    modifier: Modifier,
//    currentSelector: InputSelector,
//    onStickerClicked: (Sticker) -> Unit,
//    onMediaSelect: (List<Uri>, Boolean?) -> Unit,
//    onCall: () -> Unit,
//    onDocumentSelected: (List<Uri>) -> Unit,
//    onGifTabSelected: (Boolean) -> Unit,
//    onHideGifTab: () -> Unit,
//    mediaList: List<Pair<Uri, Long?>>,
//    selectedMedia: List<Uri>,
//    loadMedia: () -> Unit,
//    openPhoneSetting: () -> Unit
//) {
//    if (currentSelector == InputSelector.NONE || currentSelector == InputSelector.CAMERA) return
//
//    // Initial value to check the status of emoji sticker to handle the textfield error
//    var emojiStickerSelected by remember { mutableStateOf(EmojiStickerSelector.STICKER) }
//
//    // Request focus to force the TextField to lose it
//    val focusRequester = FocusRequester()
//    // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
//    // Add one more condition to handle the bad behavior of keyboard, because Gif tab is bottom sheet then
//    // there's no input selector, so it will force to close the keyboard
//    SideEffect {
//        when (currentSelector) {
//            InputSelector.EMOJI -> {
//                if (emojiStickerSelected != EmojiStickerSelector.GIF) {
//                    focusRequester.requestFocus()
//                }
//            }
//            InputSelector.EXTRA,
//            InputSelector.GALLERY -> {
//                onHideGifTab()
//                focusRequester.requestFocus()
//            }
//            else -> {}
//        }
//    }
//
//    Surface(tonalElevation = 8.dp, modifier = modifier) {
//        when (currentSelector) {
//            InputSelector.EMOJI -> EmojiSelector(
//                onStickerClicked = onStickerClicked,
//                focusRequester = focusRequester,
//                onGifTabSelected = onGifTabSelected,
//                onEmojiChange = { emojiStickerSelected = it }
//            )
//            InputSelector.EXTRA -> ExtraSelector(
//                focusRequester = focusRequester,
//                onCall = onCall,
//                onDocumentSelected = onDocumentSelected,
//            )
//            InputSelector.GALLERY -> GallerySelector(
//                focusRequester = focusRequester,
//                onMediaSelect = onMediaSelect,
//                mediaList = mediaList,
//                selectedMedia = selectedMedia,
//                loadMedia = loadMedia,
//                openPhoneSetting = openPhoneSetting
//            )
//            else -> {}
//        }
//    }
//}
//
//
//@Composable
//private fun CameraSelector(
//    onTakePhoto: () -> Unit,
//    onTakeVideo: () -> Unit,
//    onDismiss: () -> Unit,
//) {
//    AlertDialog(
//        containerColor = MaterialTheme.colorScheme.onPrimary,
//        text = {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                CameraSelectorRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    label = stringResource(R.string.chat_camera_take_photo),
//                    onClick = onTakePhoto,
//                )
//                CameraSelectorRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    label = stringResource(R.string.chat_camera_record_video),
//                    onClick = onTakeVideo,
//                )
//            }
//        },
//        dismissButton = {},
//        confirmButton = {},
//        onDismissRequest = { onDismiss() },
//    )
//}
//
//@Composable
//private fun CameraSelectorRow(
//    modifier: Modifier,
//    label: String,
//    onClick: () -> Unit,
//) {
//    TextButton(
//        onClick = onClick,
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(2.dp),
//        contentPadding = PaddingValues(vertical = 8.dp),
//        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
//    ) {
//        Text(
//            label,
//            modifier = Modifier.fillMaxWidth(),
//            style = MaterialTheme.typography.bodyLarge,
//        )
//    }
//}
//
//@Composable
//private fun GallerySelector(
//    focusRequester: FocusRequester,
//    onMediaSelect: (List<Uri>, Boolean?) -> Unit,
//    mediaList: List<Pair<Uri, Long?>>,
//    selectedMedia: List<Uri>,
//    loadMedia: () -> Unit,
//    openPhoneSetting: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .focusRequester(focusRequester)
//            .focusTarget()
//            .background(MaterialTheme.colorScheme.onPrimary),
//    ) {
//        PhotoTable(
//            mediaList = mediaList,
//            selectedMedia = selectedMedia,
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            onMediaSelect = onMediaSelect,
//            loadMedia = loadMedia,
//            openPhoneSetting = openPhoneSetting
//        )
//    }
//}
//
//@Composable
//private fun ExtraSelector(
//    focusRequester: FocusRequester,
//    onCall: () -> Unit,
//    onDocumentSelected: (List<Uri>) -> Unit,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .focusRequester(focusRequester)
//            .focusTarget()
//            .background(MaterialTheme.colorScheme.onPrimary),
//    ) {
//        ExtraTable(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            onCall = onCall,
//            onDocumentSelected = onDocumentSelected,
//        )
//    }
//}
//
//@Composable
//private fun EmojiSelector(
//    onStickerClicked: (Sticker) -> Unit,
//    focusRequester: FocusRequester,
//    onGifTabSelected: (Boolean) -> Unit,
//    onEmojiChange: (EmojiStickerSelector) -> Unit
//) {
//    var selected by remember { mutableStateOf(EmojiStickerSelector.STICKER) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .focusRequester(focusRequester)
//            .focusTarget()
//            .background(MaterialTheme.colorScheme.onPrimary),
//    ) {
//
//        EmojiSelectorTabs(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            selectedTab = selected,
//            onTabSelected = {
//                selected = it
//                onGifTabSelected(it == EmojiStickerSelector.GIF)
//                onEmojiChange(it)
//            }
//        )
//
//        when (selected) {
//            EmojiStickerSelector.STICKER -> {
//                StickerTable(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f),
//                    onStickerClicked = onStickerClicked,
//                )
//            }
//            else -> {}
//        }
//    }
//}
//
//@Composable
//private fun EmojiSelectorTabs(
//    modifier: Modifier,
//    selectedTab: EmojiStickerSelector,
//    onTabSelected: (EmojiStickerSelector) -> Unit,
//) {
//    Surface(tonalElevation = 2.dp) {
//        Row(
//            modifier = modifier
//                .background(MaterialTheme.colorScheme.onPrimary),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            EmojiSelectorItem(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        onTabSelected(EmojiStickerSelector.STICKER)
//                    },
//                isSelected = selectedTab == EmojiStickerSelector.STICKER,
//                iconSelected = R.drawable.ic_emoticon_fill,
//                icon = R.drawable.ic_emoticon_line
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//            EmojiSelectorItem(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        onTabSelected(EmojiStickerSelector.GIF)
//                    },
//                isSelected = selectedTab == EmojiStickerSelector.GIF,
//                iconSelected = R.drawable.ic_gif_fill,
//                icon = R.drawable.ic_gif_line
//            )
//        }
//    }
//}
//
//@Composable
//private fun EmojiSelectorItem(
//    modifier: Modifier,
//    isSelected: Boolean,
//    iconSelected: Int,
//    icon: Int
//) {
//    val iconSelectedColor = MaterialTheme.colorScheme.primary
//    val iconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
//    Box(
//        modifier = modifier
//            .height(40.dp),
//    ) {
//        Icon(
//            painter = painterResource(if (isSelected) iconSelected else icon),
//            contentDescription = null,
//            modifier = Modifier
//                .size(24.dp)
//                .align(Alignment.Center),
//            tint = if (isSelected) iconSelectedColor else iconColor,
//        )
//        Divider(
//            modifier = Modifier
//                .width(64.dp)
//                .height(2.dp)
//                .align(Alignment.BottomCenter)
//                .alpha(if (isSelected) 1f else 0f)
//                .background(
//                    color = MaterialTheme.colorScheme.primary,
//                    shape = RoundedCornerShape(100.dp)
//                )
//        )
//    }
//}
//
//@Preview
//@Composable
//fun ChatInputPreview() {
//    ChatInput(
//        onMessageSent = { _, _, _ -> },
//        onMessagePreview = { _, _ -> },
//        mediaList = listOf(),
//        loadMedia = {},
//        openPhoneSetting = {},
//        onGifTabSelected = {},
//    )
//}