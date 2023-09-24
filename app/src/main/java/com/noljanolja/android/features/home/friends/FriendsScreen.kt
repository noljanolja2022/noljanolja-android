package com.noljanolja.android.features.home.friends

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.EmptyPage
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.Rationale
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
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

@Composable
private fun FriendsScreenContent(
    uiState: UiState<List<User>>,
    handleEvent: (FriendsEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()

    ScaffoldWithUiState(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = "Friends",
            )
        },
        content = {
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
                        if (visibleContacts.isEmpty()) {
                            EmptyPage(message = stringResource(id = R.string.contacts_not_found))
                        } else {
                            ContactList(
                                scrollState = scrollState,
                                contacts = visibleContacts,
                                onItemClick = {
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
    )
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
            .padding(vertical = 15.dp),
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
        Expanded()
        TextButton(onClick = {
            onClick(contact)
        }) {
            Text(stringResource(R.string.friend_reference))
        }
    }
}