package com.noljanolja.android.features.home.chat.components

import android.view.Gravity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonAppBarTitle
import com.noljanolja.core.conversation.domain.model.ConversationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatDialog(
    visible: Boolean = false,
    onDismissRequest: () -> Unit,
    onNewSingleChat: (String) -> Unit,
    onNewSecretChat: (String) -> Unit,
    onNewGroupChat: (String) -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val configuration = LocalConfiguration.current
            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
            dialogWindowProvider.window.setGravity(Gravity.TOP)
            Column(
                modifier = Modifier
                    .width(configuration.screenWidthDp.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CenterAlignedTopAppBar(
                    title = { CommonAppBarTitle(stringResource(id = R.string.contacts_start_chat)) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    NewChatItem(
                        title = stringResource(id = R.string.contacts_title_normal),
                        icon = Icons.Outlined.Chat
                    ) {
                        onDismissRequest()
                        onNewSingleChat.invoke(ConversationType.SINGLE.name)
                    }
                    NewChatItem(
                        title = stringResource(id = R.string.contacts_title_secret),
                        icon = Icons.Outlined.Lock
                    ) {
                        onDismissRequest()
                    }
                    NewChatItem(
                        title = stringResource(id = R.string.contacts_title_group),
                        icon = Icons.Outlined.Forum
                    ) {
                        onDismissRequest()
                        onNewGroupChat.invoke(ConversationType.GROUP.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.NewChatItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1F)
            .clickable {
                onClick.invoke()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium)
    }
}