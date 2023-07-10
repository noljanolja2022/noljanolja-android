package com.noljanolja.android.features.home.chat.components

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.BackPressHandler
import com.noljanolja.android.util.getFileName
import com.noljanolja.android.util.getTmpFileUri
import com.noljanolja.android.util.showToast
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.media.domain.model.Sticker

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
    GIF,
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInput(
    onMessageSent: (String, MessageType, List<Uri>) -> Unit,
    selectedMedia: List<Uri>,
    modifier: Modifier = Modifier,
    shouldShowSendButton: Boolean = false,
    resetScroll: () -> Unit = {},
    mediaList: List<Pair<Uri, Long?>>,
    loadMedia: () -> Unit,
    focusRequester: FocusRequester,
    openPhoneSetting: () -> Unit,
    onChangeSelectMedia: (List<Uri>) -> Unit,
    onHandleBottomSheetBackPress: () -> Unit = {},
    onShowSticker: (Sticker?) -> Unit,
    onOpenFullImages: () -> Unit,
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    // when gif BottomSheet is expanding, if back button is pressed, collapse bottom sheet instead of
    // close selector expand
    val context = LocalContext.current

    val dismissKeyboard = {
        currentInputSelector = InputSelector.NONE
        onHandleBottomSheetBackPress()
    }
    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackPressHandler(onBackPressed = dismissKeyboard)
        if (currentInputSelector == InputSelector.KEYBOARD) {
            LocalSoftwareKeyboardController.current?.show()
            focusRequester.requestFocus()
        }
    }
    var extendActionState by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf(TextFieldValue()) }
    var attachments by remember { mutableStateOf<List<Uri>>(listOf()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val modifiableSelectMedia = mutableListOf<Uri>().apply {
        addAll(selectedMedia)
    }
    val onMediaSelect = { mediaSelect: List<Uri>, isAdd: Boolean? ->

        when (isAdd) {
            true -> modifiableSelectMedia.addAll(mediaSelect)
            false -> modifiableSelectMedia.removeAll(mediaSelect)
            else -> {
                mediaSelect.forEach { media ->
                    if (!selectedMedia.contains(media)) {
                        modifiableSelectMedia.add(media)
                    }
                }
            }
        }
        onChangeSelectMedia(modifiableSelectMedia)
    }

    Column(modifier = modifier) {
        Box {
            if (selectedMedia.isEmpty()) {
                ChatInputText(
                    textField = message,
                    onTextChanged = { message = it },
                    focusRequester = focusRequester,
                    onFocusChanged = { focused ->
                        if (focused) {
                            currentInputSelector = InputSelector.NONE
                            onHandleBottomSheetBackPress()
                            resetScroll()
                        }
                    },
                    selector = currentInputSelector,
                    onSelectorChanged = {
                        currentInputSelector = it
                    },
                    onMessageSent = {
                        onMessageSent(message.text, MessageType.PLAINTEXT, listOf())
                        // Reset text field and close keyboard
                        message = TextFieldValue()
                        // Move scroll to bottom
                        resetScroll()
                        dismissKeyboard()
                    },
                    shouldShowSendButton = shouldShowSendButton,
                    extendActionState = extendActionState,
                    toggleActionState = {
                        extendActionState = it
                        if (!extendActionState) {
                            currentInputSelector = InputSelector.NONE
                        }
                    }
                )
            } else {
                SendMedia(
                    numberSelected = selectedMedia.size,
                    onClear = { onChangeSelectMedia(emptyList()) },
                    onSendMedia = {
                        onMessageSent(
                            message.text,
                            MessageType.PHOTO,
                            selectedMedia
                        )
                        currentInputSelector = InputSelector.NONE
                        onChangeSelectMedia(emptyList())
                    }
                )
            }
        }
        SelectorExpanded(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp),
            currentSelector = currentInputSelector,
            onMediaSelect = { mediaSelect, isAdd ->
                onMediaSelect.invoke(mediaSelect, isAdd)
            },
            onStickerClicked = { id, sticker ->
                onMessageSent(
                    "$id/${sticker.imageFile.getFileName()}",
                    MessageType.STICKER,
                    listOf()
                )
            },
            onShowSticker = onShowSticker,
            onCall = {},
            mediaList = mediaList,
            selectedMedia = selectedMedia,
            loadMedia = loadMedia,
            openPhoneSetting = openPhoneSetting,
            onOpenFullImages = onOpenFullImages,
        )
        if (extendActionState && (currentInputSelector == InputSelector.NONE || currentInputSelector == InputSelector.CAMERA)) {
            ExtraActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp),
                onSelectorChanged = { currentInputSelector = it }
            )
        }
    }

    if (currentInputSelector == InputSelector.CAMERA) {
        onHandleBottomSheetBackPress()

        val selectedFile = remember { mutableStateOf<Uri?>(null) }
        val photoLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                currentInputSelector = InputSelector.NONE
                if (it) {
                    selectedFile.value?.let {
                        onMessageSent(message.text, MessageType.PHOTO, listOf(it))
                    }
                    selectedFile.value = null
                }
            }
        val videoLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) {
                currentInputSelector = InputSelector.NONE
                if (it) {
                    selectedFile.value?.let {
                        onMessageSent(message.text, MessageType.PHOTO, listOf(it))
                    }
                    selectedFile.value = null
                }
            }
        CameraSelector(
            onTakePhoto = {
                selectedFile.value = context.getTmpFileUri("temp_photo", ".png").also {
                    photoLauncher.launch(it)
                }
            },
            onTakeVideo = {
                selectedFile.value = context.getTmpFileUri("temp_video", ".mp4").also {
                    videoLauncher.launch(it)
                }
            },
            onDismiss = { currentInputSelector = InputSelector.NONE },
        )
    }
}

@Composable
private fun ChatInputText(
    textField: TextFieldValue,
    extendActionState: Boolean,
    toggleActionState: (Boolean) -> Unit,
    onTextChanged: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    selector: InputSelector,
    onSelectorChanged: (InputSelector) -> Unit,
    onMessageSent: () -> Unit,
    shouldShowSendButton: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        val focusManager = LocalFocusManager.current
        IconButton(
            onClick = {
                focusManager.clearFocus()
                toggleActionState.invoke(!extendActionState)
            },
            modifier = Modifier
                .padding(bottom = 8.dp, end = 11.dp)
                .size(24.dp)
        ) {
            Icon(
                if (extendActionState) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,

            )
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
                        toggleActionState.invoke(false)
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
                            if (state.isFocused) {
                                toggleActionState.invoke(false)
                            }
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
                    if (selector != InputSelector.EMOJI) {
                        toggleActionState.invoke(true)
                    }
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AnimatedVisibility(visible = textField.text.isNotEmpty() || shouldShowSendButton) {
                IconButton(
                    onClick = onMessageSent,
                    modifier = Modifier
                        .padding(end = 4.dp, bottom = 4.dp)
                        .then(Modifier.size(30.dp))
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SelectorExpanded(
    modifier: Modifier,
    currentSelector: InputSelector,
    onMediaSelect: (List<Uri>, Boolean?) -> Unit,
    onStickerClicked: (Long, Sticker) -> Unit,
    onShowSticker: (Sticker?) -> Unit,
    onCall: () -> Unit,
    mediaList: List<Pair<Uri, Long?>>,
    selectedMedia: List<Uri>,
    loadMedia: () -> Unit,
    openPhoneSetting: () -> Unit,
    onOpenFullImages: () -> Unit,
) {
    if (currentSelector == InputSelector.NONE || currentSelector == InputSelector.CAMERA) return

    // Initial value to check the status of emoji sticker to handle the textfield error
    var emojiStickerSelected by remember { mutableStateOf(EmojiStickerSelector.STICKER) }

    // Request focus to force the TextField to lose it
    val focusRequester = FocusRequester()
    // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
    // Add one more condition to handle the bad behavior of keyboard, because Gif tab is bottom sheet then
    // there's no input selector, so it will force to close the keyboard
    SideEffect {
        when (currentSelector) {
            InputSelector.EMOJI -> {
                if (emojiStickerSelected != EmojiStickerSelector.GIF) {
                    focusRequester.requestFocus()
                }
            }
//            InputSelector.EXTRA,
            InputSelector.GALLERY,
            -> {
                focusRequester.requestFocus()
            }

            else -> {}
        }
    }

    Surface(tonalElevation = 8.dp, modifier = modifier) {
        when (currentSelector) {
            InputSelector.GALLERY -> GallerySelector(
                focusRequester = focusRequester,
                onMediaSelect = onMediaSelect,
                mediaList = mediaList,
                selectedMedia = selectedMedia,
                loadMedia = loadMedia,
                openPhoneSetting = openPhoneSetting,
                onOpenFullImages = onOpenFullImages,
            )

            InputSelector.EMOJI -> EmojiSelector(
                onStickerClicked = onStickerClicked,
                onShowSticker = onShowSticker,
                focusRequester = focusRequester,
                onEmojiChange = { emojiStickerSelected = it }
            )

            else -> {}
        }
    }
}

@Composable
private fun ExtraActions(
    modifier: Modifier = Modifier,
    onSelectorChanged: (InputSelector) -> Unit,
) {
    val actions = listOf(
        ExtraAction(
            stringResource(id = R.string.chat_action_album),
            Icons.Outlined.Collections,
            Color(0xFF86D558)
        ) {
            onSelectorChanged(InputSelector.GALLERY)
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_camera),
            Icons.Outlined.PhotoCamera,
            Color(0xFF6892DE)
        ) {
            onSelectorChanged(InputSelector.CAMERA)
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_events),
            Icons.Outlined.EventNote,
            Color(0xFF39C65A)
        ) {
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_wallet),
            Icons.Outlined.AccountBalanceWallet,
            Color(0xFFF8DF00)
        ) {
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_location),
            Icons.Outlined.Place,
            Color(0xFF52B49D)
        ) {
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_voice_chat),
            ImageVector.vectorResource(id = R.drawable.ic_voice_chat),
            Color(0xFFFB9E65)
        ) {
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_contacts),
            Icons.Outlined.PermContactCalendar,
            Color(0xFF6595F5)
        ) {
        },
        ExtraAction(
            stringResource(id = R.string.chat_action_file),
            Icons.Outlined.AttachFile,
            Color(0xFFD47DE6)
        ) {
        },
    )
    Column(
        modifier = modifier.padding(
            start = 44.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 18.dp
        ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val row: @Composable (List<ExtraAction>) -> Unit = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                it.take(4).forEach { action ->
                    Column(
                        modifier = Modifier.weight(1F),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = action.onClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(action.color)
                        ) {
                            Icon(
                                action.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            action.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        row.invoke(actions.take(4))
        row.invoke(actions.takeLast(4))
    }
}

@Composable
private fun GallerySelector(
    focusRequester: FocusRequester,
    onMediaSelect: (List<Uri>, Boolean?) -> Unit,
    mediaList: List<Pair<Uri, Long?>>,
    selectedMedia: List<Uri>,
    loadMedia: () -> Unit,
    openPhoneSetting: () -> Unit,
    onOpenFullImages: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusTarget()
            .background(MaterialTheme.colorScheme.onPrimary),
    ) {
        PhotoTable(
            mediaList = mediaList,
            selectedMedia = selectedMedia,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onMediaSelect = onMediaSelect,
            loadMedia = loadMedia,
            openPhoneSetting = openPhoneSetting,
            onOpenFullImages = onOpenFullImages,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraSelector(
    onTakePhoto: () -> Unit,
    onTakeVideo: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var isPhoto by remember {
        mutableStateOf(false)
    }
    val cameraPermissionsState = rememberPermissionState(
        Manifest.permission.CAMERA,
        onPermissionResult = {
            when {
                !it -> context.showToast(context.getString(R.string.permission_camera))
                isPhoto -> onTakePhoto.invoke()
                else -> onTakeVideo.invoke()
            }
        }
    )
    AlertDialog(
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CameraSelectorRow(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.chat_camera_take_photo),
                    onClick = {
                        isPhoto = true
                        if (cameraPermissionsState.status != PermissionStatus.Granted) {
                            cameraPermissionsState.launchPermissionRequest()
                        } else {
                            onTakePhoto.invoke()
                        }
                    },
                )
//                CameraSelectorRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    label = stringResource(R.string.chat_camera_record_video),
//                    onClick = onTakeVideo,
//                )
            }
        },
        dismissButton = {},
        confirmButton = {},
        onDismissRequest = { onDismiss() },
    )
}

@Composable
private fun EmojiSelector(
    onStickerClicked: (Long, Sticker) -> Unit,
    onShowSticker: (Sticker?) -> Unit,
    focusRequester: FocusRequester,
    onGifTabSelected: (Boolean) -> Unit = {},
    onEmojiChange: (EmojiStickerSelector) -> Unit,
) {
    var selected by remember { mutableStateOf(EmojiStickerSelector.STICKER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusTarget()
            .background(MaterialTheme.colorScheme.background),
    ) {
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

        when (selected) {
            EmojiStickerSelector.STICKER -> {
                StickerTable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onStickerClicked = onStickerClicked,
                    onShowSticker = onShowSticker,
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun EmojiSelectorTabs(
    modifier: Modifier,
    selectedTab: EmojiStickerSelector,
    onTabSelected: (EmojiStickerSelector) -> Unit,
) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmojiSelectorItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onTabSelected(EmojiStickerSelector.STICKER)
                    },
                isSelected = selectedTab == EmojiStickerSelector.STICKER,
                iconSelected = Icons.Filled.EmojiEmotions,
                icon = Icons.Outlined.EmojiEmotions
            )
            Spacer(modifier = Modifier.width(4.dp))
            EmojiSelectorItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onTabSelected(EmojiStickerSelector.GIF)
                    },
                isSelected = selectedTab == EmojiStickerSelector.GIF,
                iconSelected = Icons.Filled.Gif,
                icon = Icons.Outlined.Gif
            )
        }
    }
}

@Composable
private fun EmojiSelectorItem(
    modifier: Modifier,
    isSelected: Boolean,
    iconSelected: ImageVector,
    icon: ImageVector,
) {
    val iconSelectedColor = MaterialTheme.colorScheme.onBackground
    val iconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    Box(
        modifier = modifier
            .height(40.dp),
    ) {
        Icon(
            if (isSelected) iconSelected else icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
            tint = if (isSelected) iconSelectedColor else iconColor,
        )
        Divider(
            modifier = Modifier
                .width(64.dp)
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .alpha(if (isSelected) 1f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(100.dp)
                )
        )
    }
}

@Composable
private fun CameraSelectorRow(
    modifier: Modifier,
    label: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(
            label,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SendMedia(
    numberSelected: Int,
    onClear: () -> Unit,
    onSendMedia: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.CenterEnd,
    ) {
        IconButton(
            onClick = onClear,
            modifier = Modifier.align(Alignment.CenterStart)
                .padding(start = 12.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
        Text(
            stringResource(id = R.string.args_chat_selected, numberSelected),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(.87F),
            modifier = Modifier
                .align(Alignment.Center)

        )
        IconButton(
            onClick = onSendMedia,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .then(Modifier.size(30.dp))
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp)

        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}

data class ExtraAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit,
)