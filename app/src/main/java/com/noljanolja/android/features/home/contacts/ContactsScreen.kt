package com.noljanolja.android.features.home.contacts

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.android.ui.composable.*
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.user.domain.model.User
import io.ktor.http.*
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContactsScreen(
    type: ConversationType,
    conversationId: Long,
    viewModel: ContactsViewModel = getViewModel { parametersOf(type, conversationId) },
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val selectedContacts by viewModel.selectedUserFlow.collectAsStateWithLifecycle()

    ContactsScreenContent(
        type = type,
        conversationId = conversationId,
        uiState = uiState,
        selectedContacts = selectedContacts,
        handleEvent = viewModel::handleEvent,
    )
}

@Composable
fun ContactsScreenContent(
    type: ConversationType,
    conversationId: Long,
    uiState: UiState<List<User>>,
    selectedContacts: List<User>,
    handleEvent: (ContactsEvent) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    Surface(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithUiState(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            topBar = {
                CommonTopAppBar(
                    title = stringResource(
                        id = when (type) {
                            ConversationType.SINGLE -> R.string.contacts_title_normal
                            ConversationType.GROUP -> {
                                if (conversationId > 0L) {
                                    R.string.contacts_title_add_memmber
                                } else {
                                    R.string.contacts_title_group
                                }
                            }
                            else -> R.string.contacts_title_secret
                        }
                    ),
                    actions = {
                        if (type == ConversationType.GROUP) {
                            TextButton(
                                onClick = { handleEvent(ContactsEvent.ConfirmContacts) },
                                enabled = selectedContacts.isNotEmpty(),
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                            ) {
                                Text(stringResource(id = R.string.common_agree))
                            }
                        }
                    },
                    onBack = {
                        handleEvent(ContactsEvent.Back)
                    }
                )
            },
            content = {
                when {
                    !PermissionChecker(LocalContext.current).canReadContacts() -> {
                        Rationale(
                            modifier = Modifier.fillMaxSize(),
                            permissions = mapOf(
                                android.Manifest.permission.READ_CONTACTS to stringResource(
                                    id = R.string.permission_contacts_description
                                )
                            ),
                            onSuccess = {
                                handleEvent(ContactsEvent.SyncContacts)
                            },
                            openPhoneSettings = {
                                handleEvent(ContactsEvent.OpenPhoneSettings)
                            }
                        )
                    }
                    !uiState.loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                        ) {
                            SearchBar(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                searchText = searchText,
                                hint = stringResource(R.string.common_search),
                                onSearch = {
                                    searchText = it
                                    handleEvent(ContactsEvent.SearchContact(it))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            val visibleContacts = uiState.data.orEmpty().filter { user ->
                                with(searchText.trim()) {
                                    user.name.contains(
                                        this,
                                        true
                                    ) ||
                                        user.phone!!.contains(
                                            this,
                                            true
                                        )
                                }
                            }
                            if (selectedContacts.isNotEmpty()) {
                                LazyRow() {
                                    items(selectedContacts, key = { it.id }) { contact ->
                                        SelectedContact(contact = contact) {
                                            handleEvent(ContactsEvent.SelectContact(contact))
                                        }
                                        Spacer(modifier = Modifier.width(20.dp))
                                    }
                                }
                                Divider(
                                    modifier = Modifier.padding(vertical = 15.dp),
                                    thickness = 1.dp
                                )
                            }
                            if (visibleContacts.isEmpty()) {
                                EmptyPage(message = stringResource(id = R.string.contacts_not_found))
                            } else {
                                Text(
                                    stringResource(id = R.string.common_friends),
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                ContactList(
                                    type = type,
                                    scrollState = scrollState,
                                    contacts = visibleContacts,
                                    selectedContacts = selectedContacts,
                                    onItemClick = {
                                        handleEvent(ContactsEvent.SelectContact(it))
                                    },
                                    loadMoreContacts = {
                                        handleEvent(ContactsEvent.LoadMore)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ContactList(
    type: ConversationType,
    contacts: List<User>,
    scrollState: LazyListState,
    selectedContacts: List<User>,
    onItemClick: (User) -> Unit,
    loadMoreContacts: () -> Unit,
) {
    LazyColumn(
        state = scrollState
    ) {
        items(contacts, key = { it.id }) { contact ->
            ContactRow(
                type = type,
                contact = contact,
                selected = selectedContacts.contains(contact)
            ) { onItemClick(it) }
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = loadMoreContacts)
}

@Composable
fun ContactRow(
    type: ConversationType,
    contact: User,
    selected: Boolean = false,
    onClick: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(contact) }
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
        if (type == ConversationType.GROUP) {
            val modifier = Modifier.size(20.dp)
            if (selected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = modifier,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = modifier,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun SelectedContact(contact: User, onRemove: () -> Unit) {
    Box() {
        OvalAvatar(user = contact, modifier = Modifier.padding(top = 5.dp, end = 5.dp))
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Filled.Cancel,
                contentDescription = null,
            )
        }
    }
}