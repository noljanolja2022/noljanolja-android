package com.noljanolja.android.features.home.chat_options

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.withSemiBold
import com.noljanolja.android.util.showToast
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.isAdminOfConversation
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatOptionsScreen(
    conversationId: Long,
    viewModel: ChatOptionsViewModel = getViewModel { parametersOf(conversationId) },
) {
    val context = LocalContext.current
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val isAdminOfConversation by viewModel.isAdminOfConversation.collectAsStateWithLifecycle()
    var showSuccessToast by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = viewModel.updateConversationSuccessEvent) {
        viewModel.updateConversationSuccessEvent.collect {
            showSuccessToast = true
        }
    }
    LaunchedEffect(key1 = viewModel.errorFlow, block = {
        viewModel.errorFlow.collect {
            it.printStackTrace()
            context.showToast(it.message)
        }
    })
    ChatOptionsContent(
        uiState = uiState,
        isAdmin = isAdminOfConversation,
        handleEvent = viewModel::handleEvent
    )
    ComposeToast(
        isVisible = showSuccessToast,
        onDismiss = { showSuccessToast = false }
    ) {
        SuccessToast()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatOptionsContent(
    uiState: UiState<Conversation>,
    isAdmin: Boolean,
    handleEvent: (ChatOptionsEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var selectParticipant by remember {
        mutableStateOf<User?>(null)
    }
    val bottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var showLeaveChatDialog by remember {
        mutableStateOf(false)
    }

    var showRemoveParticipantChatDialog by remember {
        mutableStateOf(false)
    }

    var showAssignAdminChatDialog by remember {
        mutableStateOf(false)
    }
    var showBlockParticipantDialog by remember {
        mutableStateOf(false)
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            EditParticipantSheet(
                participant = selectParticipant,
                isAdmin = isAdmin,
                onChat = {},
                onAssignAdmin = {
                    showAssignAdminChatDialog = true
                },
                onBlock = {
                    showBlockParticipantDialog = true
                },
                onRemove = {
                    showRemoveParticipantChatDialog = true
                }
            )
        }
    ) {
        ScaffoldWithUiState(
            topBar = {
                CommonTopAppBar(
                    onBack = {
                        handleEvent(ChatOptionsEvent.Back)
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            uiState = uiState
        ) {
            uiState.data?.let { conversation ->
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1F)
                    ) {
                        if (conversation.type == ConversationType.GROUP) {
                            groupContent(
                                conversation = conversation,
                                isAdmin = isAdmin,
                                onUserClick = {
                                    scope.launch {
                                        selectParticipant = it
                                        bottomSheetState.show()
                                    }
                                },
                                onAddParticipant = {
                                    handleEvent(ChatOptionsEvent.AddContact)
                                },
                                onChangeTitle = {
                                    handleEvent(ChatOptionsEvent.EditTitle)
                                },
                                onShowMedia = {
                                    handleEvent(ChatOptionsEvent.ShowMedias)
                                }
                            )
                        } else {
                            singleContent(
                                conversation = conversation,
                                onBlock = { user ->
                                    selectParticipant = user
                                    showBlockParticipantDialog = true
                                },
                                onShowMedia = {
                                    handleEvent(ChatOptionsEvent.ShowMedias)
                                }
                            )
                        }
                    }
                    LeaveChatRow {
                        showLeaveChatDialog = true
                    }
                }
            }
        }
    }
    WarningDialog(
        isWarning = showLeaveChatDialog,
        title = stringResource(id = R.string.edit_chat_warning_leave_title),
        content = stringResource(id = R.string.edit_chat_warning_leave_description),
        dismissText = stringResource(id = R.string.common_no),
        confirmText = stringResource(id = R.string.common_yes),
        onDismiss = { showLeaveChatDialog = false },
        onConfirm = {
            handleEvent(ChatOptionsEvent.LeaveConversation)
            showLeaveChatDialog = false
        }
    )
    WarningDialog(
        isWarning = showAssignAdminChatDialog,
        title = stringResource(id = R.string.edit_chat_warning_admin_title),
        content = stringResource(id = R.string.edit_chat_warning_admin_description),
        dismissText = stringResource(id = R.string.common_no),
        confirmText = stringResource(id = R.string.common_yes),
        onDismiss = { showAssignAdminChatDialog = false },
        onConfirm = {
            scope.launch {
                bottomSheetState.hide()
            }
            selectParticipant?.let {
                handleEvent(ChatOptionsEvent.MakeAdminConversation(it.id))
            }
            showAssignAdminChatDialog = false
        }
    )
    WarningDialog(
        isWarning = showBlockParticipantDialog,
        title = stringResource(id = R.string.edit_chat_warning_block_title),
        content = stringResource(id = R.string.edit_chat_warning_block_description),
        dismissText = stringResource(id = R.string.common_no),
        confirmText = stringResource(id = R.string.common_yes),
        onDismiss = { showBlockParticipantDialog = false },
        onConfirm = {
            selectParticipant?.let {
                handleEvent(ChatOptionsEvent.BlockUser(it.id))
            }
            showBlockParticipantDialog = false
        }
    )
    WarningDialog(
        isWarning = showRemoveParticipantChatDialog,
        title = stringResource(id = R.string.edit_chat_warning_remove_title),
        content = stringResource(id = R.string.edit_chat_warning_remove_description),
        dismissText = stringResource(id = R.string.common_no),
        confirmText = stringResource(id = R.string.common_yes),
        onDismiss = { showRemoveParticipantChatDialog = false },
        onConfirm = {
            scope.launch {
                bottomSheetState.hide()
            }
            selectParticipant?.let {
                handleEvent(ChatOptionsEvent.RemoveParticipant(it.id))
            }
            showRemoveParticipantChatDialog = false
        }
    )
}

private fun LazyListScope.groupContent(
    conversation: Conversation,
    isAdmin: Boolean,
    onUserClick: (User) -> Unit,
    onAddParticipant: () -> Unit,
    onChangeTitle: () -> Unit,
    onShowMedia: () -> Unit,
) {
    chatParticipants(
        conversation = conversation,
        isAdmin = isAdmin,
        onUserClick = onUserClick,
        onAddParticipant = onAddParticipant
    )
    item {
        Divider(thickness = 1.dp)
    }
    groupSettings(
        onChangeTitle = onChangeTitle,
        onShowMedia = onShowMedia
    )
}

private fun LazyListScope.singleContent(
    conversation: Conversation,
    onBlock: (User) -> Unit,
    onShowMedia: () -> Unit,
) {
    val participant = conversation.participants.firstOrNull { !it.isMe } ?: return
    item {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            OvalAvatar(user = participant, size = 64.dp, modifier = Modifier.padding(top = 35.dp))
            SizeBox(height = 12.dp)
            Text(
                participant.name,
                style = MaterialTheme.typography.bodyLarge.withSemiBold(),
                color = MaterialTheme.colorScheme.onBackground,
            )
            SizeBox(height = 25.dp)
        }
    }
    item {
        Divider(thickness = 8.dp, color = MaterialTheme.colorScheme.surface)
    }
    item {
        SettingRow(
            text = stringResource(id = R.string.edit_chat_block_user),
            icon = Icons.Default.Block,
            onClick = {
                onBlock(participant)
            }
        )
    }
    item {
        SettingRow(
            text = "Medias, Files, Links",
            icon = Icons.Default.PermMedia,
            onClick = onShowMedia
        )
    }
}

fun LazyListScope.chatParticipants(
    conversation: Conversation,
    isAdmin: Boolean,
    onUserClick: (User) -> Unit,
    onAddParticipant: () -> Unit,
) {
    item {
        Text(
            stringResource(id = R.string.common_members),
            modifier = Modifier.padding(
                top = 12.dp,
                start = 16.dp,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    if (isAdmin) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onAddParticipant.invoke()
                    }
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .border(
                            width = 1.dp,
                            MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.edit_chat_add_members),
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
    conversation.participants.forEach {
        item {
            ParticipantRow(
                participant = it,
                isAdmin = it.isAdminOfConversation(conversation),
                onClick = onUserClick
            )
        }
    }
}

fun LazyListScope.groupSettings(
    onChangeTitle: () -> Unit,
    onShowMedia: () -> Unit,
) {
    item {
        Text(
            stringResource(id = R.string.common_setting),
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 16.dp,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    item {
        SettingRow(
            text = stringResource(id = R.string.edit_chat_change_room_name),
            icon = Icons.Default.Edit,
            onClick = onChangeTitle
        )
    }
    item {
        SettingRow(
            text = "Medias, Files, Links",
            icon = Icons.Default.PermMedia,
            onClick = onShowMedia
        )
    }
}

@Composable
private fun SettingRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clickable { onClick.invoke() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
        Divider()
    }
}

@Composable
private fun ParticipantRow(
    participant: User,
    isAdmin: Boolean,
    onClick: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(participant) }
            .padding(
                vertical = 10.dp,
                horizontal = 16.dp
            )
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val titleStyle = MaterialTheme.typography.titleMedium
        val descriptionStyle =
            MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline)
        OvalAvatar(user = participant)
        Spacer(modifier = Modifier.width(16.dp))
        Column() {
            if (participant.isMe) {
                Text(
                    stringResource(id = R.string.common_you),
                    style = titleStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Text(
                participant.name,
                style = if (participant.isMe) descriptionStyle else titleStyle
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        if (isAdmin) {
            Text(
                stringResource(id = R.string.common_admin),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF34C759))
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun LeaveChatRow(
    onLeave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                vertical = 10.dp,
                horizontal = 16.dp
            )
    ) {
        Button(
            onClick = onLeave,
            colors = with(MaterialTheme.colorScheme) {
                ButtonDefaults.buttonColors(
                    containerColor = background,
                    contentColor = error
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(stringResource(id = R.string.edit_chat_leave_chat).uppercase())
        }
    }
}

@Composable
private fun EditParticipantSheet(
    participant: User?,
    isAdmin: Boolean,
    onChat: () -> Unit,
    onAssignAdmin: () -> Unit,
    onBlock: (User) -> Unit,
    onRemove: () -> Unit,
) {
    if (participant == null) return
    val style = MaterialTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OvalAvatar(user = participant)
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            participant.name,
            style = style.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(id = R.string.common_chat),
            style = style.bodyLarge,
            modifier = Modifier.clickable {
                onChat.invoke()
            },
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (isAdmin) {
            if (!participant.isMe) {
                Text(
                    stringResource(id = R.string.edit_chat_make_admin),
                    style = style.bodyLarge,
                    modifier = Modifier.clickable {
                        onAssignAdmin.invoke()
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(
                stringResource(id = R.string.edit_chat_block_user),
                style = style.bodyLarge,
                modifier = Modifier.clickable {
                    onBlock.invoke(participant)
                },
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                stringResource(id = R.string.edit_chat_remove_user),
                style = style.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                modifier = Modifier.clickable {
                    onRemove.invoke()
                },
            )
        }
    }
}

@Composable
fun BoxScope.SuccessToast() {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(84.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8F))
    ) {
        Icon(
            Icons.Rounded.Done,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
            tint = MaterialTheme.colorScheme.background
        )
    }
}