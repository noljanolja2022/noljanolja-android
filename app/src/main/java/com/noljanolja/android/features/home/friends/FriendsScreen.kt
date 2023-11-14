package com.noljanolja.android.features.home.friends

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.android.ui.composable.EmptyPage
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.LoadingDialog
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.Rationale
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    FriendsScreenContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendsScreenContent(
    uiState: UiState<List<User>>,
    handleEvent: (FriendsEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    handleEvent(FriendsEvent.AddFriend)
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.person_add),
                        contentDescription = null,
                    )
                },
                text = { Text(text = stringResource(R.string.add_friend_title)) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { padding ->
        Image(
            painter = painterResource(R.drawable.bg_with_circle),
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FriendHeading(onAddFriend = {
                handleEvent(FriendsEvent.AddFriend)
            })
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp,
                ),
            ) {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    LoadingDialog(isLoading = uiState.loading)
                    when {
                        !PermissionChecker(LocalContext.current).canReadContacts() -> {
                            Rationale(
                                modifier = Modifier.fillMaxSize(),
                                permissions = mapOf(
                                    Manifest.permission.READ_CONTACTS to stringResource(
                                        id = R.string.permission_contacts_description
                                    )
                                ),
                                onSuccess = {
                                    handleEvent(FriendsEvent.SyncContacts)
                                },
                                openPhoneSettings = {
                                    handleEvent(FriendsEvent.OpenPhoneSettings)
                                }
                            )
                        }

                        !uiState.loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                            ) {
                                val visibleContacts = uiState.data.orEmpty()
                                SizeBox(height = 22.dp)
                                Row {
                                    Text(
                                        stringResource(R.string.common_friends),
                                        style = MaterialTheme.typography.bodyLarge.withBold()
                                    )
                                    Expanded()
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                        }
                                    )
                                }
                                if (visibleContacts.isEmpty()) {
                                    EmptyPage(message = stringResource(id = R.string.contacts_not_found))
                                } else {
                                    ContactList(
                                        scrollState = scrollState,
                                        contacts = visibleContacts,
                                        onItemClick = {
                                            handleEvent(
                                                FriendsEvent.OpenFriendOption(
                                                    friendId = it.id,
                                                    friendName = it.name,
                                                    friendAvatar = it.avatar ?: ""
                                                )
                                            )
                                        },
                                        loadMoreContacts = {
                                            handleEvent(FriendsEvent.LoadMore)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendHeading(onAddFriend: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Expanded()
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            SizeBox(width = 20.dp)
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        SizeBox(height = 5.dp)
        Text(
            stringResource(R.string.earn_point_title),
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        SizeBox(height = 10.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .clickable { onAddFriend() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.invite_friends_to_get_benefits),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge.withBold()
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }
        SizeBox(height = 10.dp)
    }
}

@Composable
private fun ContactList(
    contacts: List<User>,
    scrollState: LazyListState,
    onItemClick: (User) -> Unit,
    loadMoreContacts: () -> Unit,
) {
    LazyColumn(
        state = scrollState
    ) {
        items(contacts, key = { it.id }) { contact ->
            ContactRow(
                contact = contact,
            ) { onItemClick(it) }
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = loadMoreContacts)
}

@Composable
private fun ContactRow(
    contact: User,
    onClick: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 15.dp)
            .clickable { onClick(contact) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OvalAvatar(user = contact)
        Text(
            text = contact.name,
            modifier = Modifier
                .padding(start = 15.dp)
                .weight(1F),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}