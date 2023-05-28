package com.noljanolja.android.features.home.chat.components

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonAppBarTitle
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.darkContent
import com.noljanolja.core.conversation.domain.model.ConversationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatDialog(
    visible: Boolean = false,
    onDismissRequest: () -> Unit,
    onNewSingleChat: (String) -> Unit,
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
                    title = {
                        CommonAppBarTitle(
                            stringResource(id = R.string.contacts_start_chat),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 10.dp)
                ) {
                    NewChatItem(
                        title = stringResource(id = R.string.contacts_title_normal),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_chat),
                        background = MaterialTheme.colorScheme.primary
                    ) {
                        onDismissRequest()
                        onNewSingleChat.invoke(ConversationType.SINGLE.name)
                    }
                    SizeBox(width = 10.dp)
                    NewChatItem(
                        title = stringResource(id = R.string.contacts_title_group),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_chat_bubble),
                        size = 21.dp,
                        background = MaterialTheme.colorScheme.secondary,
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
    background: Color,
    size: Dp = 16.dp,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1F)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(size),
            tint = MaterialTheme.darkContent()
        )
        SizeBox(width = 10.dp)
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.darkContent()
        )
    }
}