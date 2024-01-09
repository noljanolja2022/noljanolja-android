package com.noljanolja.android.features.home.sendpoint

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.extensions.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.BUTTON_HEIGHT
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VERTICAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import com.noljanolja.core.loyalty.domain.model.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

/**
 * Created by tuyen.dang on 1/2/2024.
 */

@Composable
internal fun SendPointScreen(
    friendId: String,
    friendName: String,
    friendAvatar: String,
    isRequestPoint: Boolean,
    viewModel: SendPointViewModel = getViewModel { parametersOf(friendId) }
) {
    viewModel.run {
        val memberInfo by memberInfoFlow.collectAsStateWithLifecycle()
        val checkPointValid by checkPointValid.collectAsStateWithLifecycle()
        val isLoading by isLoading.collectAsStateWithLifecycle()
        val context = LocalContext.current
        LaunchedEffect(sendSuccessEvent) {
            sendSuccessEvent.collectLatest { isRequestPointDone ->
                isRequestPointDone?.let {
                    context.showToast(
                        context.getString(
                            if (it) R.string.request_point_success_message else R.string.send_point_success_message
                        )
                    )
                    handleEvent(SendPointEvent.Back)
                }
            }
        }
        SendPointScreenContent(
            memberInfo = memberInfo,
            friendName = friendName,
            friendAvatar = friendAvatar,
            isRequestPoint = isRequestPoint,
            checkPointValid = checkPointValid,
            isLoading = isLoading,
            handleEvent = ::handleEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SendPointScreenContent(
    memberInfo: MemberInfo,
    friendName: String,
    friendAvatar: String,
    isRequestPoint: Boolean,
    checkPointValid: Boolean?,
    isLoading: Boolean,
    handleEvent: (SendPointEvent) -> Unit,
) {
    var point by remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(
                    id = if (isRequestPoint) R.string.add_friend_request_point else R.string.add_friend_send_point
                ),
                onBack = {
                    handleEvent(SendPointEvent.Back)
                },
                centeredTitle = true
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { padding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxSize()
                .padding(padding)
        ) {
            MarginVertical(13)
            MyPoint(
                point = memberInfo.point,
                modifier = Modifier
                    .padding(horizontal = PADDING_VIEW_SCREEN.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            MarginVertical(18)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PADDING_VIEW_SCREEN.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Yellow00)
                    .padding(horizontal = 40.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarginVertical(10)
                OvalAvatar(
                    size = 40.dp,
                    radius = 14.dp,
                    avatar = friendAvatar
                )
                MarginVertical(5)
                Text(
                    text = stringResource(
                        id = if (isRequestPoint) R.string.request_point_title else R.string.send_point_title,
                        friendName
                    ),
                    style = Typography.titleLarge,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                MarginVertical(20)
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp)),
                    value = point,
                    onValueChange = {
                        if (it.length < 10) point = it
                    },
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    minHeight = (40 * getScaleSize()).dp,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            handleEvent(
                                SendPointEvent.CheckValidPoint(
                                    point = point.toLongOrNull(),
                                    isRequestPoint = isRequestPoint
                                )
                            )
                        }
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Orange00,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 0.dp,
                        top = 0.dp
                    )
                )
                MarginVertical(15)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (checkPointValid == false) stringResource(R.string.request_point_error) else "",
                    style = Typography.titleLarge,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.systemRed100()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
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
                    enabled = point.isNotBlank(),
                    title = stringResource(id = R.string.common_continue).uppercase(),
                    bgColor = MaterialTheme.colorScheme.primary,
                    height = BUTTON_HEIGHT,
                    textColor = Color.Black
                ) {
                    handleEvent(
                        SendPointEvent.CheckValidPoint(
                            point = point.toLongOrNull(),
                            isRequestPoint = isRequestPoint
                        )
                    )
                }
            }
        }
        if (checkPointValid == true && !isLoading) {
            DialogWarningWithContent(
                dismissText = stringResource(id = R.string.common_no).uppercase(),
                confirmText = stringResource(id = R.string.common_yes).uppercase(),
                onDismiss = {
                    handleEvent(SendPointEvent.HideDialog)
                },
                onConfirm = {
                    handleEvent(
                        SendPointEvent.SendPoint(
                            point = point.toLongOrNull(),
                            isRequestPoint = isRequestPoint
                        )
                    )
                },
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.img_cash),
                        contentDescription = null,
                        modifier = Modifier
                            .height(178.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillHeight
                    )
                    MarginVertical(20)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (isRequestPoint) {
                            stringResource(
                                id = R.string.request_point_dialog_title,
                                point.toLongOrNull() ?: 0L,
                                friendName
                            )
                        } else {
                            stringResource(
                                id = R.string.send_point_dialog_title,
                                point,
                                friendName
                            )
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                    )
                    MarginVertical(10)
                    if (isRequestPoint) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(
                                id = R.string.request_point_dialog_message,
                                friendName
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.bodyMedium.run {
                                        SpanStyle(
                                            fontSize = fontSize,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = fontWeight
                                        )
                                    }
                                ) {
                                    append(stringResource(id = R.string.send_point_dialog_message_part_1))
                                }
                                append(" ")
                                withStyle(
                                    style = MaterialTheme.typography.bodyMedium.run {
                                        SpanStyle(
                                            fontSize = fontSize,
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                ) {
                                    append("-$point")
                                }
                                append(" ")
                                withStyle(
                                    style = MaterialTheme.typography.bodyMedium.run {
                                        SpanStyle(
                                            fontSize = fontSize,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = fontWeight
                                        )
                                    }
                                ) {
                                    append(stringResource(id = R.string.send_point_dialog_message_part_2))
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            )
        }
        if (isLoading) {
            LoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
 