package com.noljanolja.android.features.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContent(
    uiState: SettingUIState,
    handleEvent: (SettingEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.common_setting),
                onBack = {
                    handleEvent(SettingEvent.Back)
                },
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
//            ListTileWithToggleButton(
//                modifier = Modifier
//                    .padding(horizontal = 20.dp)
//                    .padding(
//                        top = 18.dp,
//                        bottom = 12.dp,
//                    ),
//                title = {
//                    Text(
//                        text = stringResource(id = R.string.setting_push_notification),
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            color = MaterialTheme.secondaryTextColor(),
//                        ),
//                    )
//                },
//                checked = uiState.allowPushNotification,
//                onCheckedChange = {
//                    handleEvent(SettingEvent.TogglePushNotification)
//                },
//            )
//            PrimaryDivider(
//                modifier = Modifier.height(4.dp),
//            )
//            PrimaryListTile(
//                modifier = Modifier
//                    .padding(vertical = 5.dp, horizontal = 20.dp)
//                    .padding(top = 12.dp, bottom = 18.dp),
//                title = {
//                    Text(
//                        text = stringResource(id = R.string.setting_clear_cache_data),
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            color = MaterialTheme.secondaryTextColor(),
//                        ),
//                    )
//                },
//                trailingDrawable = R.drawable.ic_forward,
//                onClick = { handleEvent(SettingEvent.ClearCacheData) },
//            )
//            PrimaryListTile(
//                modifier = Modifier
//                    .padding(vertical = 5.dp, horizontal = 20.dp)
//                    .padding(bottom = 12.dp),
//                title = {
//                    Text(
//                        text = stringResource(id = R.string.setting_open_source_license),
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            color = MaterialTheme.secondaryTextColor(),
//                        ),
//                    )
//                },
//                trailingDrawable = R.drawable.ic_forward,
//                onClick = { handleEvent(SettingEvent.ClearCacheData) },
//            )
//            PrimaryDivider(
//                modifier = Modifier.height(4.dp),
//            )
//            CommonListTile(
//                modifier = Modifier
//                    .padding(vertical = 5.dp, horizontal = 20.dp)
//                    .padding(top = 12.dp),
//                title = {
//                    Text(
//                        text = stringResource(
//                            id = R.string.setting_current_version,
//                            uiState.versionName,
//                        ),
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            color = MaterialTheme.colorScheme.outline,
//                        ),
//                    )
//                },
//            )
            Expanded()
            PrimaryButton(
                text = stringResource(id = R.string.common_log_out).uppercase(),
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                onClick = {
                    handleEvent(SettingEvent.Logout)
                }
            )
        }
    }
}
