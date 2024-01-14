package com.noljanolja.android.features.home.friend_notification

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import org.koin.androidx.compose.*

/**
 * Created by tuyen.dang on 1/14/2024.
 */

@Composable
fun FriendNotificationScreen(
    viewModel: FriendNotificationViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    FriendNotificationContent(
        uiState = uiState,
        isLoading = isLoading,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendNotificationContent(
    uiState: FriendNotificationUiState,
    isLoading: Boolean,
    handleEvent: (FriendNotificationEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(id = R.string.notifications_title),
                centeredTitle = true,
                onBack = {
                    if (!isLoading) handleEvent(FriendNotificationEvent.GoBack)
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MarginVertical(5)
            if (uiState.notificationsUnRead.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.common_new),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                uiState.notificationsUnRead.forEach {
                    NotificationItem(item = it)
                }
                MarginVertical(20)
            }
            if (uiState.notificationsRead.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.common_previous),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                uiState.notificationsRead.forEach {
                    NotificationItem(item = it)
                }
            }
        }
        if (isLoading) {
            LoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}