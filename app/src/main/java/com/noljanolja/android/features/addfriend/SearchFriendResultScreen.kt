package com.noljanolja.android.features.addfriend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.LoadingDialog
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.getErrorDescription
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.showToast
import com.noljanolja.core.user.domain.model.User

@Composable
fun SearchFriendResultScreen(addFriendViewModel: AddFriendViewModel) {
    val context = LocalContext.current
    val searchUsersResult by addFriendViewModel.searchUsersResultFlow.collectAsStateWithLifecycle()
    val isLoading by addFriendViewModel.isProcessingInvite.collectAsStateWithLifecycle()
    LaunchedEffect(addFriendViewModel.errorFlow) {
        addFriendViewModel.errorFlow.collect {
            context.showToast(context.getErrorDescription(it))
        }
    }
    SearchFriendResultContent(
        users = searchUsersResult,
        handleEvent = addFriendViewModel::handleEvent
    )
    LoadingDialog(isLoading = isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFriendResultContent(
    users: List<User>,
    handleEvent: (AddFriendEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.add_friend_search_by_phone),
                onBack = {
                    handleEvent(AddFriendEvent.Back)
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 24.dp, start = 28.dp, end = 4.dp)
        ) {
            items(users) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OvalAvatar(user = it, size = 40.dp)
                    SizeBox(width = 16.dp)
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = it.phone.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.secondaryTextColor()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable {
                                handleEvent(AddFriendEvent.AddFriend(it.id, it.name))
                            }
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(vertical = 7.dp, horizontal = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.background
                        )
                        SizeBox(width = 5.dp)
                        Text(
                            text = stringResource(id = R.string.add_friend_chat_now),
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }
}