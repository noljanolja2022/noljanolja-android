package com.noljanolja.android.features.setting

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.extensions.convertToString
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.ui.composable.ButtonRadius
import com.noljanolja.android.ui.composable.ButtonTextWithToggle
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ComposeToast
import com.noljanolja.android.ui.composable.MarginVertical
import com.noljanolja.android.ui.composable.RankingRow
import com.noljanolja.android.ui.composable.WarningDialog
import com.noljanolja.android.ui.theme.PrimaryGreen
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_HORIZONTAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VERTICAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.android.util.loadFileInfo
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    var isShowSuccessToast by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(
        key1 = viewModel.updateUserEvent,
        block = {
            viewModel.updateUserEvent.collect {
                isShowSuccessToast = it
            }
        }
    )
    SettingContent(
        uiState = uiState,
        memberInfo = memberInfo,
        handleEvent = viewModel::handleEvent,
    )
    ComposeToast(
        isVisible = isShowSuccessToast,
        onDismiss = {
            isShowSuccessToast = false
        },
        content = {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                    .align(Alignment.Center)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContent(
    uiState: SettingUIState,
    memberInfo: MemberInfo,
    handleEvent: (SettingEvent) -> Unit,
) {
    val user = uiState.user
    var isShowLogoutDialog by remember {
        mutableStateOf(false)
    }
    var isShowClearCatchDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = avatar, block = {
        context.loadFileInfo(avatar)?.let {
            handleEvent(
                SettingEvent.ChangeAvatar(
                    it
                )
            )
        }
    })
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
                        .padding(
                            vertical = PADDING_VIEW_SCREEN.dp,
                            horizontal = PADDING_VERTICAL_SCREEN.dp
                        )
                ) {
                    val (
                        imgAvar,
                        btnChange,
                        tvTitleRanking,
                        tvMemberRank,
                        tvTitleName,
                        tvName,
                        tvTitlePhone,
                        tvPhone,
                        tvTileGender,
                        tvGender,
                        horizontalChain
                    ) = createRefs()
                    CircleAvatar(
                        modifier = Modifier.constrainAs(imgAvar) {
                            top.linkTo(parent.top, PADDING_VIEW_SCREEN.dp)
                            linkTo(start = parent.start, end = parent.end)
                        },
                        user = user ?: User(),
                        size = 52.dp
                    )
                    Box(
                        modifier = Modifier
                            .heightIn(min = 26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                showAvatarInputDialog = true
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
                    Text(
                        text = stringResource(id = R.string.my_ranking_title),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTitleRanking) {
                            start.linkTo(parent.start)
                            top.linkTo(btnChange.bottom, PADDING_HORIZONTAL_SCREEN.dp)
                        },
                    )
                    RankingRow(
                        tier = memberInfo.currentTier,
                        onClick = {},
                        modifier = Modifier.constrainAs(tvMemberRank) {
                            linkTo(horizontalChain.end, parent.end)
                            linkTo(tvTitleRanking.top, tvTitleRanking.bottom)
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.setting_name),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTitleName) {
                            start.linkTo(parent.start)
                            top.linkTo(tvMemberRank.bottom, 15.dp)
                        },
                    )
                    Text(
                        text = user?.name.convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvName) {
                            start.linkTo(horizontalChain.end, 20.dp)
                            linkTo(tvTitleName.top, tvTitleName.bottom)
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.setting_phone_number),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTitlePhone) {
                            start.linkTo(parent.start)
                            top.linkTo(tvName.bottom, 15.dp)
                        },
                    )
                    Spacer(
                        modifier = Modifier.constrainAs(horizontalChain) {
                            start.linkTo(tvTitlePhone.end)
                            top.linkTo(parent.top)
                        }
                    )
                    Text(
                        text = user?.phone?.hidePhoneNumber().convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvPhone) {
                            start.linkTo(horizontalChain.end, 20.dp)
                            linkTo(tvTitlePhone.top, tvTitlePhone.bottom)
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.update_profile_gender),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTileGender) {
                            start.linkTo(parent.start)
                            top.linkTo(tvPhone.bottom, 15.dp)
                        },
                    )
                    Text(
                        text = user?.gender?.name.convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvGender) {
                            start.linkTo(horizontalChain.end, 20.dp)
                            linkTo(tvTileGender.top, tvTileGender.bottom)
                        }
                    )
                }
                MarginVertical(5)
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
                        isShowClearCatchDialog = true
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
                        vertical = PADDING_VERTICAL_SCREEN.dp
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
    AvatarInput(
        isShown = showAvatarInputDialog,
        onAvatarInput = { uri ->
            uri?.let { avatar = it }
            showAvatarInputDialog = false
        },
    )
    WarningDialog(
        title = stringResource(R.string.confirm_clear_cache_title),
        content = stringResource(R.string.confirm_clear_cache_message),
        isWarning = isShowClearCatchDialog,
        dismissText = stringResource(R.string.common_no),
        confirmText = stringResource(R.string.common_yes),
        onDismiss = {
            isShowClearCatchDialog = false
        },
        onConfirm = {
            isShowClearCatchDialog = false
            handleEvent(SettingEvent.ClearCacheData)
        }
    )
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