package com.noljanolja.android.features.home.contacts

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.ui.composable.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    ContactsScreenContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ContactsScreenContent(
    uiState: UiState<List<Contact>>,
    handleEvent: (ContactsEvent) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val contactsPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CONTACTS
    ) { result ->
        if (result) {
            handleEvent(ContactsEvent.SyncContacts)
        } else {
            openDialog = true
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithUiState(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            topBar = {
                CommonTopAppBar(
                    title = stringResource(id = R.string.contacts_title),
                    onBack = {
                        handleEvent(ContactsEvent.Back)
                    }
                )
            },
            content = {
                when {
                    !contactsPermissionState.status.isGranted -> {
                        Rationale(
                            modifier = Modifier.fillMaxSize(),
                            permissions = mapOf(
                                android.Manifest.permission.READ_CONTACTS to stringResource(
                                    id = R.string.permission_contacts_description
                                )
                            ),
                            onRequestPermission = { contactsPermissionState.launchPermissionRequest() }
                        )
                    }
                    !uiState.loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            SearchBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                searchText = searchText,
                                hint = stringResource(R.string.common_search),
                                onSearch = { text -> searchText = text }
                            )
                            val visibleContacts = uiState.data.orEmpty().filter { contact ->
                                with(searchText.trim()) {
                                    contact.name.contains(
                                        this,
                                        true
                                    ) || contact.phones.any { phone ->
                                        phone.contains(
                                            this,
                                            true
                                        )
                                    }
                                }
                            }
                            if (visibleContacts.isEmpty()) {
                                EmptyPage(message = stringResource(id = R.string.contacts_not_found))
                            } else {
                                ContactList(contacts = visibleContacts) {
                                }
                            }
                        }
                    }
                }
            }
        )

        WarningDialog(
            title = null,
            content = stringResource(R.string.permission_required_description),
            isWarning = openDialog,
            dismissText = stringResource(R.string.common_cancel),
            confirmText = stringResource(R.string.permission_go_to_settings),
            onDismiss = {
                openDialog = false
            },
            onConfirm = {
                openDialog = false
                handleEvent(ContactsEvent.OpenPhoneSettings)
            }
        )
    }
}

@Composable
fun ContactList(
    contacts: List<Contact>,
    onItemClick: (Contact) -> Unit,
) {
    LazyColumn {
        items(contacts, key = { it.id }) { contact ->
            ContactRow(contact) { onItemClick(it) }
            Divider(modifier = Modifier.padding(start = 72.dp))
        }
    }
}

@Composable
fun ContactRow(
    contact: Contact,
    onClick: (Contact) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable { onClick(contact) }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                ImageRequest.Builder(context = context)
                    .data(contact.getAvatarUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .fallback(R.drawable.placeholder_avatar)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Text(
            text = contact.name,
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1F),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}