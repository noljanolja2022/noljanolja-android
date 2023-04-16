package com.noljanolja.android.features.edit_chat_title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditChatTitleScreen(
    conversationId: Long,
    viewModel: EditChatTitleViewModel = getViewModel { parametersOf(conversationId) },
) {
    EditChatTitleContent(
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditChatTitleContent(
    handleEvent: (EditChatTitleEvent) -> Unit,
) {
    var title by remember {
        mutableStateOf("")
    }
    ScaffoldWithUiState(uiState = UiState<Unit>(), topBar = {
        CommonTopAppBar(
            title = "Change nickname",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onBack = {
                handleEvent(EditChatTitleEvent.Back)
            }
        )
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            EditTitleField(title = title, onChange = { title = it })
            Spacer(modifier = Modifier.weight(1F))
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.common_save)
            ) {
                handleEvent(EditChatTitleEvent.ConfirmEditChatTitle(title))
            }
        }
    }
}

@Composable
private fun EditTitleField(title: String, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 14.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .heightIn(min = 36.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1F)) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                value = title,
                onValueChange = onChange,
            )
        }
        Text("${title.length}/40")
    }
}