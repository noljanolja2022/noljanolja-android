package com.noljanolja.android.features.home.friends

import android.Manifest
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.services.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.core.user.domain.model.*
import org.koin.androidx.compose.*

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = getViewModel(),
) {
    viewModel.run {
        val uiState by uiStateFlow.collectAsStateWithLifecycle()
        val userStateFlow by userStateFlow.collectAsStateWithLifecycle()
        val needReadStateFlow by needReadStateFlow.collectAsStateWithLifecycle()
        FriendsScreenContent(
            uiState = uiState,
            userStateFlow = userStateFlow,
            needReadNotification = needReadStateFlow,
            handleEvent = ::handleEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendsScreenContent(
    uiState: UiState<List<User>>,
    userStateFlow: User,
    needReadNotification: Boolean,
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
                containerColor = YellowMain,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(Yellow01)
                .fillMaxSize()
                .padding(padding)
        ) {
            CommonAppBarSearch(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 13.dp,
                            bottomEnd = 13.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(top = 16.dp, bottom = 6.dp),
                hintSearch = stringResource(id = R.string.friends_search_friends),
                searchFieldBackground = NeutralLight.copy(0.7f),
                onSearchFieldClick = {
                    handleEvent(FriendsEvent.OpenSearchScreen)
                },
                icon = if(needReadNotification) {
                    ImageVector.vectorResource(
                        id = R.drawable.ic_unread
                    )
                } else  {
                    Icons.Filled.Notifications
                },
                iconTint = Color.Black,
                onIconClick = {
                    handleEvent(FriendsEvent.OpenNotificationScreen)
                },
                textColor = NeutralDarkGrey.copy(alpha = 0.38f),
                avatar = userStateFlow.avatar,
                onAvatarClick = {
                    handleEvent(FriendsEvent.OpenSettingScreen)
                }
            )
            MarginVertical(8)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.earn_point_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )
            MarginVertical(8)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PADDING_VIEW_SCREEN.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                    .clickable {
                        handleEvent(FriendsEvent.InviteFriend)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.invite_friends_to_get_benefits),
                    color = YellowMain,
                    style = MaterialTheme.typography.bodyLarge.withBold()
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = YellowMain,
                    modifier = Modifier.size(24.dp)
                )
            }
            MarginVertical(20)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
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