package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.noljanolja.android.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Rationale(
    modifier: Modifier,
    permissions: Map<String, String>,
    onSuccess: () -> Unit,
    openPhoneSettings: () -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    val permissionStates = rememberPermissionState(
        permissions.keys.first()
    ) { result ->
        if (result) {
            onSuccess.invoke()
        } else {
            openDialog = true
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.permission),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                permissions.values.first(),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    permissionStates.launchPermissionRequest()
                },
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp),
            ) {
                Text(text = stringResource(R.string.permission_accept))
            }
        }
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
                openPhoneSettings.invoke()
            }
        )
    }
}
