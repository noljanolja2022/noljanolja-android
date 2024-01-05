package com.noljanolja.android.features.home.friendoption

import android.app.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.ads.nativeads.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.core.loyalty.domain.model.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*

/**
 * Created by tuyen.dang on 11/14/2023.
 */

@Composable
fun FriendOptionScreen(
    friendId: String,
    friendName: String,
    friendAvatar: String,
    viewModel: FriendOptionViewModel = getViewModel { parametersOf(friendId, friendName) }
) {
    viewModel.run {
        val memberInfo by memberInfoFlow.collectAsStateWithLifecycle()
        FriendOptionContent(
            handleEvent = ::handleEvent,
            memberInfo = memberInfo,
            name = friendName,
            avatar = friendAvatar
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendOptionContent(
    handleEvent: (FriendOptionEvent) -> Unit,
    memberInfo: MemberInfo,
    name: String,
    avatar: String
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onBack = {
                    handleEvent(FriendOptionEvent.GoBack)
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyPoint(
                point = memberInfo.point,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            )
            MarginVertical(20)
            OvalAvatar(
                modifier = Modifier
                    .fillMaxHeight(0.26f)
                    .aspectRatio(1f)
                    .heightIn(min = 0.dp, max = 200.dp),
                radius = 13.dp,
                avatar = avatar
            )
            MarginVertical(5)
            Text(
                text = name,
                style = Typography.titleLarge,
                color = Color.Black
            )
            MarginVertical(50)
//            ButtonRadius(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                title = stringResource(id = R.string.add_friend_chat_now).uppercase(),
//                bgColor = PictonBlue,
//                icon = painterResource(id = R.drawable.ic_chat)
//            ) {
//                handleEvent(FriendOptionEvent.GoToChatScreen)
//            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                ButtonRadius(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    title = stringResource(id = R.string.add_friend_request_point).uppercase(),
                    bgColor = Orange00,
                    textColor = Color.Black
                ) {
                    handleEvent(
                        FriendOptionEvent.GoToSendPointScreen(
                            friendAvatar = avatar,
                            isRequestPoint = true
                        )
                    )
                }
                MarginHorizontal(12)
                ButtonRadius(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    title = stringResource(id = R.string.add_friend_send_point).uppercase(),
                    bgColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.Black
                ) {
                    handleEvent(
                        FriendOptionEvent.GoToSendPointScreen(
                            friendAvatar = avatar,
                            isRequestPoint = false
                        )
                    )
                }
            }
            Expanded()
            (context as? Activity)?.let {
                DynamicNative().render(
                    DefaultMediumNative2(context = it),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        }
    }
}
