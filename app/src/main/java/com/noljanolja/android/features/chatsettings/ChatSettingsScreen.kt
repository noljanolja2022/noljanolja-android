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
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.features.chatsettings.composable.ChatProfile
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ComposeToast
import com.noljanolja.android.ui.composable.ListTileWithToggleButton
import com.noljanolja.android.ui.composable.PrimaryListTile
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withSemiBold
import com.noljanolja.android.util.loadFileInfo
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import org.koin.androidx.compose.getViewModel

@Composable
fun ChatSettingsScreen(
    viewModel: ChatSettingsViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
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
        memberInfo = memberInfo,
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
                    modifier = Modifier
                        .size(64.dp)
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
    memberInfo: MemberInfo,
    handleEvent: (ChatSettingsEvent) -> Unit,
) {
    val context = LocalContext.current
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = avatar, block = {
        context.loadFileInfo(avatar)?.let {
            handleEvent(
                ChatSettingsEvent.ChangeAvatar(
                    it
                )
            )
        }
    })
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.common_setting),
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
        Column(modifier = Modifier.padding(16.dp)) {
            ChatProfile(
                user = user,
                avatar = avatar,
                memberInfo = memberInfo,
                onChangeAvatar = {
                    showAvatarInputDialog = true
                }
            )
            SizeBox(height = 10.dp)
            Text(
                stringResource(id = R.string.chat_setting_friend_management),
                modifier = Modifier.padding(vertical = 9.dp),
                style = MaterialTheme.typography.bodyLarge.withSemiBold()
            )
            ListTileWithToggleButton(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_setting_auto_add_friend),
                        fontSize = 13.sp
                    )
                },
                checked = true,
                onCheckedChange = {}
            )
            SizeBox(height = 10.dp)
            Text(
                stringResource(id = R.string.chat_setting_auto_add_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.secondaryTextColor()
            )
            SizeBox(height = 20.dp)
            PrimaryListTile(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_setting_all_friends),
                        fontSize = 13.sp
                    )
                },
                trailingDrawable = R.drawable.ic_forward
            )
            SizeBox(height = 20.dp)
            PrimaryListTile(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_setting_hide_friends),
                        fontSize = 13.sp
                    )
                },
                trailingDrawable = R.drawable.ic_forward
            )
            SizeBox(height = 20.dp)
            PrimaryListTile(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_setting_blocked_friends),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                trailingDrawable = R.drawable.ic_forward
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