package com.noljanolja.android.features.sharemessage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.EmptyPage
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelectShareMessageScreen(
    selectMessageId: Long,
    fromConversationId: Long,
    viewModel: SelectShareMessageViewModel = getViewModel {
        parametersOf(
            selectMessageId,
        )
    },
) {
    val context = LocalContext.current
    viewModel.setContext(context)
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val selectedContacts by viewModel.selectedContactFlow.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel.sharedEvent) {
        viewModel.sharedEvent.collectLatest {
            context.showToast(context.getString(R.string.common_share_success))
        }
    }
    SelectShareMessageContent(
        uiState = uiState,
        selectedContacts = selectedContacts,
        fromConversationId = fromConversationId,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun SelectShareMessageContent(
    uiState: UiState<List<ShareContact>>,
    selectedContacts: List<ShareContact>,
    fromConversationId: Long,
    handleEvent: (SelectShareMessageEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()
    Surface(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithUiState(
            modifier = Modifier.fillMaxSize(),
            showContentWithLoading = true,
            uiState = uiState,
            topBar = {
                CommonTopAppBar(
                    onBack = {
                        handleEvent(SelectShareMessageEvent.Back)
                    },
                    actions = {
                        TextButton(
                            onClick = { handleEvent(SelectShareMessageEvent.Share) },
                            enabled = selectedContacts.isNotEmpty(),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(stringResource(id = R.string.common_agree))
                        }
                    },
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                ) {
                    if (uiState.loading && uiState.data.isNullOrEmpty()) return@Column
                    Spacer(modifier = Modifier.height(16.dp))
                    val visibleContacts =
                        uiState.data?.filter { it.conversationId != fromConversationId }.orEmpty()
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
                            scrollState = scrollState,
                            contacts = visibleContacts,
                            selectedContacts = selectedContacts,
                            onItemClick = {
                                handleEvent(SelectShareMessageEvent.Select(it))
                            },
                            loadMoreContacts = {
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun ContactList(
    contacts: List<ShareContact>,
    scrollState: LazyListState,
    selectedContacts: List<ShareContact>,
    onItemClick: (ShareContact) -> Unit,
    loadMoreContacts: () -> Unit,
) {
    LazyColumn(
        state = scrollState
    ) {
        items(contacts) { contact ->
            ContactRow(
                contact = contact,
                selected = selectedContacts.contains(contact)
            ) { onItemClick(it) }
        }
    }
    InfiniteListHandler(scrollState, onLoadMore = loadMoreContacts)
}

@Composable
private fun ContactRow(
    contact: ShareContact,
    selected: Boolean = false,
    onClick: (ShareContact) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(contact) }
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val context = LocalContext.current
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = context)
                .data(contact.avatar)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .fallback(R.drawable.placeholder_avatar)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = contact.title,
            modifier = Modifier
                .padding(start = 15.dp)
                .weight(1F),
            style = MaterialTheme.typography.bodyLarge,
        )

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