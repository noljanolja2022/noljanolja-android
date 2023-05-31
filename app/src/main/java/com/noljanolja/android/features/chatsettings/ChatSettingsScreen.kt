package com.noljanolja.android.features.chatsettings

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.features.chatsettings.composable.ChatProfile
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ComposeToast
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.util.loadFileInfo
import org.koin.androidx.compose.getViewModel

@Composable
fun ChatSettingsScreen(
    viewModel: ChatSettingsViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    var isShowSuccessToast by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = viewModel.updateUserEvent, block = {
        viewModel.updateUserEvent.collect {
            isShowSuccessToast = it
        }
    })

    ChatSettingsContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
    ComposeToast(
        isVisible = isShowSuccessToast,
        onDismiss = {
            isShowSuccessToast = false
        },
        content = {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                    .align(Alignment.Center)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    )
}

@Composable
private fun ChatSettingsContent(
    uiState: UiState<ChatSettingsUiData>,
    handleEvent: (ChatSettingsEvent) -> Unit,
) {
    val context = LocalContext.current
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = avatar, block = {
        avatar?.let {
            handleEvent(
                ChatSettingsEvent.ChangeAvatar(
                    context.loadFileInfo(it)
                )
            )
        }
    })
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.chat_settings_title),
                centeredTitle = true,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onBack = {
                    handleEvent(ChatSettingsEvent.Back)
                }
            )
        }
    ) {
        val user = uiState.data?.user ?: return@ScaffoldWithUiState
        val memberInfo = uiState.data.memberInfo
        Column(modifier = Modifier.padding(16.dp)) {
            ChatProfile(
                user = user,
                avatar = avatar,
                memberInfo = memberInfo,
                onChangeAvatar = {
                    showAvatarInputDialog = true
                }
            )
        }
    }
    AvatarInput(
        isShown = showAvatarInputDialog,
        onAvatarInput = { uri ->
            uri?.let { avatar = it }
            showAvatarInputDialog = false
        },
    )
}