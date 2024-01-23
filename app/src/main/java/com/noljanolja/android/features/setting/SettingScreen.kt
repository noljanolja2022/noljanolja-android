package com.noljanolja.android.features.setting

import android.net.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.*
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.enums.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.auth.updateprofile.components.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_HORIZONTAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VERTICAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    val user by viewModel.userStateFlow.collectAsStateWithLifecycle()
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
        user = user,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun SettingContent(
    uiState: SettingUIState,
    user: User,
    memberInfo: MemberInfo,
    handleEvent: (SettingEvent) -> Unit,
) {
    var isShowLogoutDialog by remember {
        mutableStateOf(false)
    }
    var isShowClearCatchDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferenceHelper: SharedPreferenceHelper = get()
    val showChangeAvatarBottomSheet =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    var tempAvatar by rememberSaveable { mutableStateOf<Uri?>(null) }

    BackPressHandler {
        if (showChangeAvatarBottomSheet.currentValue == ModalBottomSheetValue.HalfExpanded) {
            scope.launch {
                showChangeAvatarBottomSheet.hide()
            }
        } else {
            handleEvent(SettingEvent.Back)
        }
    }

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
                        tvTitleMail,
                        tvMail,
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
                        user = user,
                        size = 52.dp
                    )
                    Box(
                        modifier = Modifier
                            .heightIn(min = 26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
//                                showAvatarInputDialog = true
                                scope.launch {
                                    showChangeAvatarBottomSheet.show()
                                }
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
                            style = MaterialTheme.typography.bodySmall.withBold(),
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
                            top.linkTo(tvTitleRanking.bottom, 15.dp)
                        },
                    )
                    Text(
                        text = user.name.convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvName) {
                            linkTo(start = horizontalChain.end, startMargin = 10.dp, end = parent.end)
                            linkTo(tvTitleName.top, tvTitleName.bottom)
                            baseline.linkTo(tvTitleName.baseline)
                            width = Dimension.fillToConstraints
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.setting_email),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTitleMail) {
                            start.linkTo(parent.start)
                            top.linkTo(tvName.bottom, 15.dp)
                        },
                    )
                    Text(
                        text = user.email.convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvMail) {
                            linkTo(start = horizontalChain.end, startMargin = 10.dp, end = parent.end)
                            linkTo(tvTitleMail.top, tvTitleMail.bottom)
                            baseline.linkTo(tvTitleMail.baseline)
                            width = Dimension.fillToConstraints
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.setting_phone_number),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.constrainAs(tvTitlePhone) {
                            start.linkTo(parent.start)
                            top.linkTo(tvMail.bottom, 15.dp)
                        },
                    )
                    Spacer(
                        modifier = Modifier.constrainAs(horizontalChain) {
                            start.linkTo(tvTitlePhone.end)
                            top.linkTo(parent.top)
                        }
                    )
                    Text(
                        text = user.phone.convertToString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvPhone) {
                            linkTo(start = horizontalChain.end, startMargin = 10.dp, end = parent.end)
                            linkTo(tvTitlePhone.top, tvTitlePhone.bottom)
                            baseline.linkTo(tvTitlePhone.baseline)
                            width = Dimension.fillToConstraints
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
                        text = user.gender?.name.convertToString().lowercase().capitalizeLetterAt(0),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.constrainAs(tvGender) {
                            linkTo(start = horizontalChain.end, startMargin = 10.dp, end = parent.end)
                            linkTo(tvTileGender.top, tvTileGender.bottom)
                            baseline.linkTo(tvTileGender.baseline)
                            width = Dimension.fillToConstraints
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
//                MarginVertical(PADDING_VIEW)
//                ButtonTextWithToggle(
//                    title = stringResource(id = R.string.setting_open_source_licence),
//                    onClick = {
//                        handleEvent(SettingEvent.Licence)
//                    }
//                )
                MarginVertical(PADDING_VIEW)
                ButtonTextWithToggle(
                    title = stringResource(id = R.string.setting_faq),
                    onClick = {
                        handleEvent(SettingEvent.FAQ)
                    }
                )
                MarginVertical(32)
                Text(
                    text = "App colors",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(PADDING_VIEW)
                ColorButton(
                    title = "Default",
                    isSelected = sharedPreferenceHelper.appColor != EAppColorSetting.KEY_ELEGANT_BLUE_COLOR
                            && sharedPreferenceHelper.appColor != EAppColorSetting.KEY_WARM_GOLD_COLOR,
                    color = Green200Main,
                    onClick = {
                        if (sharedPreferenceHelper.appColor != EAppColorSetting.KEY_DEFAULT_COLOR) {
                            sharedPreferenceHelper.appColor = EAppColorSetting.KEY_DEFAULT_COLOR
                            context.castTo<MainActivity>()?.setAppColorId(
                                EAppColorSetting.KEY_DEFAULT_COLOR
                            )
                        }
                    }
                )
                MarginVertical(PADDING_VIEW)
                ColorButton(
                    title = "Elegant blue",
                    isSelected = sharedPreferenceHelper.appColor == EAppColorSetting.KEY_ELEGANT_BLUE_COLOR,
                    color = Blue200Main,
                    onClick = {
                        if (sharedPreferenceHelper.appColor != EAppColorSetting.KEY_ELEGANT_BLUE_COLOR) {
                            sharedPreferenceHelper.appColor =
                                EAppColorSetting.KEY_ELEGANT_BLUE_COLOR
                            context.castTo<MainActivity>()?.setAppColorId(
                                EAppColorSetting.KEY_ELEGANT_BLUE_COLOR
                            )
                        }
                    }
                )
                MarginVertical(PADDING_VIEW)
                ColorButton(
                    title = "Warm gold",
                    isSelected = sharedPreferenceHelper.appColor == EAppColorSetting.KEY_WARM_GOLD_COLOR,
                    color = Gold200Main,
                    onClick = {
                        if (sharedPreferenceHelper.appColor != EAppColorSetting.KEY_WARM_GOLD_COLOR) {
                            sharedPreferenceHelper.appColor = EAppColorSetting.KEY_WARM_GOLD_COLOR
                            context.castTo<MainActivity>()?.setAppColorId(
                                EAppColorSetting.KEY_WARM_GOLD_COLOR
                            )
                        }
                    }
                )
                MarginVertical(10)
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
                MarginVertical(30)
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
                    bgColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.Black
                ) {
                    isShowLogoutDialog = true
                }
            }
        }
    }
//    AvatarInput(
//        isShown = showAvatarInputDialog,
//        onAvatarInput = { uri ->
//            uri?.let { avatar = it }
//            showAvatarInputDialog = false
//        },
//    )
    BottomSheetAvatarInput(
        sheetState = showChangeAvatarBottomSheet,
        onAvatarInput = { uri ->
            uri?.let { tempAvatar = it }
            scope.launch {
                showChangeAvatarBottomSheet.hide()
            }
        },
    )
    WarningDialog(
        modifier = Modifier.padding(horizontal = 20.dp),
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

    WarningDialog(
        title = stringResource(id = R.string.ask_to_change_avatar),
        content = stringResource(R.string.ask_to_change_avatar_massage),
        dismissText = stringResource(R.string.common_no),
        confirmText = stringResource(R.string.common_yes),
        isWarning = tempAvatar != null,
        onDismiss = {
            avatar = null
            tempAvatar = null
        },
        onConfirm = {
            avatar = tempAvatar
            tempAvatar = null
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