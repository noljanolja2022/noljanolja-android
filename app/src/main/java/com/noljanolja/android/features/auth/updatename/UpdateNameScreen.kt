package com.noljanolja.android.features.auth.updatename

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import com.noljanolja.core.user.domain.model.*
import org.koin.androidx.compose.*

/**
 * Created by tuyen.dang on 1/28/2024.
 */

@Composable
fun UpdateNameScreen(
    viewModel: UpdateNameViewModel = getViewModel(),
) {
    viewModel.run {
        val isLoading by isLoading.collectAsStateWithLifecycle()
        val user by userStateFlow.collectAsStateWithLifecycle()
        UpdateNameContent(
            isLoading = isLoading,
            user = user,
            handleEvent = viewModel::handleEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateNameContent(
    isLoading: Boolean,
    user: User,
    handleEvent: (UpdateNameEvent) -> Unit
) {
    var name by remember {
        mutableStateOf(user.name)
    }
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.setting_name),
                centeredTitle = true,
                onBack = {
                    handleEvent(UpdateNameEvent.Back)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MarginVertical(22)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (name.isNotBlank()) {
                        IconButton(onClick = {
                            name = ""
                        }) {
                            Icon(Icons.Outlined.Cancel, contentDescription = null)
                        }
                    }
                },
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            MarginVertical(11)
            Text(
                text = stringResource(id = R.string.change_name_message_1),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            MarginVertical(16)
            Text(
                text = stringResource(id = R.string.change_name_message_2),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Expanded()
            ButtonRadius(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.common_done).uppercase(),
                bgColor = MaterialTheme.colorScheme.primary,
                enabled = name.isNotBlank(),
                textColor = Color.Black
            ) {
                handleEvent(UpdateNameEvent.UpdateName(name))
            }
            MarginVertical(24)
        }
    }
    LoadingDialog(isLoading = isLoading)
}
 