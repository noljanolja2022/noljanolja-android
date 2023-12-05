package com.noljanolja.android.features.setting

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.*
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.core.user.domain.model.*
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
    val user = uiState.user
    var isShowLogoutDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.common_setting),
                centeredTitle = true,
                onBack = {
                    handleEvent(SettingEvent.Back)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = PADDING_VIEW_SCREEN.dp
                    )
            ) {
                MarginVertical(10)
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(5.dp),
                        )
                        .height(200.dp),
                ) {
                    val (imgAvar, btnChange) = createRefs()
                    user?.avatar?.let {
                        SubcomposeAsyncImage(
                            it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .constrainAs(imgAvar) {
                                    top.linkTo(parent.top, PADDING_VIEW_SCREEN.dp)
                                    linkTo(start = parent.start, end = parent.end)
                                },
                            contentScale = ContentScale.Crop,
                        )
                    } ?: CircleAvatar(user = user ?: User(), size = 52.dp)
                    Box(
                        modifier = Modifier
                            .heightIn(min = 26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {

                            }
                            .padding(vertical = 3.dp, horizontal = 13.dp)
                            .constrainAs(btnChange) {
                                top.linkTo(imgAvar.bottom, 10.dp)
                                linkTo(start = imgAvar.start, end = imgAvar.end)
                            }
                    ) {
                        Text(
                            text = stringResource(id = R.string.chat_settings_change_avatar),
                            modifier = Modifier
                                .align(
                                    Alignment.Center,
                                ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                PrimaryListTile(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                        .padding(top = 18.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.setting_exchange_account_management),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    trailingDrawable = R.drawable.ic_forward,
                )
                CommonListTile(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.setting_name),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    trailing = {
                        Row {
                            Text(
                                user?.name.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.withBold()
                            )
                            SizeBox(width = 24.dp)
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = null
                            )
                        }
                    }
                )
                CommonListTile(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.setting_phone_number),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    trailing = {
                        Text(
                            user?.phone?.hidePhoneNumber().orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.withBold()
                        )
                    }
                )
                ButtonTextWithToggle(
                    title = stringResource(id = R.string.setting_push_notification),
                    checked = uiState.allowPushNotification,
                    onCheckedChange = {
                        handleEvent(SettingEvent.TogglePushNotification)
                    },
                )
                MarginVertical(PADDING_VIEW)
                ButtonTextWithToggle(
                    title = stringResource(R.string.setting_clear_cache_data),
                    onClick = {
                        handleEvent(SettingEvent.ClearCacheData)
                    }
                )
                MarginVertical(PADDING_VIEW)
                ButtonTextWithToggle(
                    title = stringResource(id = R.string.setting_open_source_licence),
                    onClick = {
                        handleEvent(SettingEvent.Licence)
                    }
                )
                MarginVertical(PADDING_VIEW)
                ButtonTextWithToggle(
                    title = stringResource(id = R.string.setting_faq),
                    onClick = {
                        handleEvent(SettingEvent.FAQ)
                    }
                )
                MarginVertical(40)
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(
                        id = R.string.setting_current_version,
                        BuildConfig.VERSION_NAME
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    ),
                    textAlign = TextAlign.Center
                )
                MarginVertical(20)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        horizontal = PADDING_VIEW_SCREEN.dp,
                        vertical = Constant.DefaultValue.PADDING_VERTICAL_SCREEN.dp
                    )
            ) {
                ButtonRadius(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.common_log_out).uppercase(),
                    bgColor = PrimaryGreen,
                    textColor = Color.Black
                ) {
                    isShowLogoutDialog = true
                }
            }
        }
    }

    if (isShowLogoutDialog) {
        WarningDialog(
            title = null,
            content = stringResource(id = R.string.ask_to_logout),
            dismissText = stringResource(R.string.common_cancel),
            confirmText = stringResource(R.string.common_yes),
            isWarning = isShowLogoutDialog,
            onDismiss = {
                isShowLogoutDialog = false
            },
            onConfirm = {
                isShowLogoutDialog = false
                handleEvent(SettingEvent.Logout)
            }
        )
    }
}

private fun String.hidePhoneNumber(): String {
    return (0..this.length - 3).joinToString("") { "*" } + this.takeLast(3)
}