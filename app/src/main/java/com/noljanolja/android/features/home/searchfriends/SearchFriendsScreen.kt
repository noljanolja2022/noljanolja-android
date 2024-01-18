package com.noljanolja.android.features.home.searchfriends

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.core.user.domain.model.*
import org.koin.androidx.compose.*

/**
 * Created by tuyen.dang on 1/16/2024.
 */

@Composable
fun SearchFriendsScreen(viewModel: SearchFriendsViewModel = getViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val searchKeys by viewModel.searchKeys.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    SearchFriendsContent(
        uiState = uiState,
        searchKeys = searchKeys,
        isLoading = isLoading,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun SearchFriendsContent(
    uiState: UiState<SearchFriendsUiData>,
    searchKeys: List<String>,
    isLoading: Boolean,
    handleEvent: (SearchFriendsEvent) -> Unit,
) {
    var isSearchFocus by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    var searchText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SearchFriendsHeader(
            searchText = searchText,
            onSearchChange = {
                searchText = it
            },
            onFocusChange = {
                isSearchFocus = it
            },
            onBack = {
                handleEvent(SearchFriendsEvent.Back)
            },
            onSubmit = {
                handleEvent(SearchFriendsEvent.Search(searchText))
            }
        )
        if (isSearchFocus) {
            SearchFriendsHistory(
                searchKeys = searchKeys,
                onClear = {
                    handleEvent(SearchFriendsEvent.Clear(it))
                },
                onClearAll = {
                    handleEvent(SearchFriendsEvent.ClearAll)
                },
                onSearch = {
                    handleEvent(SearchFriendsEvent.Search(it))
                    searchText = it
                    focusManager.clearFocus()
                }
            )
        } else {
            if (searchText.isNotBlank() && uiState.data?.friends.isNullOrEmpty() && !isLoading) {
                EmptyPage(
                    message = stringResource(id = R.string.contacts_not_found_search),
                    textColor = Color.Red
                )
            } else {
                SearchFriendsResult(
                    friends = uiState.data?.friends.orEmpty(),
                    onClick = {
                        handleEvent(
                            SearchFriendsEvent.OpenFriendOption(
                                friendId = it.id,
                                friendName = it.name,
                                friendAvatar = it.avatar ?: ""
                            )
                        )
                    }
                )
            }
        }
    }
    LoadingDialog(isLoading = isLoading)
}

@Composable
private fun SearchFriendsHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    onBack.invoke()
                }
            )
            SizeBox(width = 15.dp)
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                searchText = searchText,
                hint = stringResource(id = R.string.search_friend_hint),
                onSearch = onSearchChange,
                background = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                onFocusChange = {
                    onFocusChange.invoke(it.isFocused)
                },
                onSearchButton = {
                    if (searchText.isNotBlank()) {
                        onSubmit()
                        focusManager.clearFocus()
                    }
                },
                focusRequester = focusRequester
            )
        }
    }
}

@Composable
private fun SearchFriendsHistory(
    searchKeys: List<String>,
    onSearch: (String) -> Unit,
    onClear: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    if (searchKeys.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SizeBox(height = 10.dp)
        Text(
            text = stringResource(id = R.string.shop_clear_all),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClearAll.invoke() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        searchKeys.forEach {
            SizeBox(height = 10.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onSearch(it)
                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_schedule),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = NeutralGrey
                )
                SizeBox(width = 10.dp)
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Expanded()
                Icon(
                    Icons.Rounded.Cancel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClear.invoke(it) },
                    tint = NeutralGrey
                )
            }
        }
    }
}

@Composable
private fun SearchFriendsResult(
    friends: List<User>,
    onClick: (User) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        items(friends, key = { it.id }) { contact ->
            ContactRow(
                contact = contact,
            ) { onClick(it) }
        }
    }
}

@Composable
private fun ContactRow(
    contact: User,
    onClick: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
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
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
 