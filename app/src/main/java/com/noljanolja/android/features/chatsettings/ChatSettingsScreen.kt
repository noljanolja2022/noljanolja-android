package com.noljanolja.android.features.chatsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.RankingRow
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun ChatSettingsScreen(
    viewModel: ChatSettingsViewModel = getViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ChatSettingsContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun ChatSettingsContent(
    uiState: UiState<ChatSettingsUiData>,
    handleEvent: (ChatSettingsEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.chat_settings_title),
                centeredTitle = true,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onBack = {
                    handleEvent(ChatSettingsEvent.Back)
                }
            )
        }
    ) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        val user = uiState.data.user
        val memberInfo = uiState.data.memberInfo
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                CircleAvatar(user = user, size = 52.dp)
                SizeBox(width = 10.dp)
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(user.name)
                    SizeBox(height = 2.dp)
                    RankingRow(tier = memberInfo.currentTier, onClick = {})
                }
            }
            SizeBox(height = 10.dp)
            PrimaryButton(
                text = stringResource(id = R.string.chat_settings_change_avatar),
                contentPadding = PaddingValues(horizontal = 13.dp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.height(22.dp)
            ) {

            }
        }
    }
}