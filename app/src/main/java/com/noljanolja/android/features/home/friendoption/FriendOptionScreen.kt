package com.noljanolja.android.features.home.friendoption

import android.app.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.ads.nativeads.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.loyalty.domain.model.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*
import kotlin.math.*

/**
 * Created by tuyen.dang on 11/14/2023.
 */

@Composable
fun FriendOptionScreen(
    friendId: String,
    friendName: String,
    friendAvatar: String,
    viewModel: FriendOptionViewModel = getViewModel { parametersOf(friendId) }
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
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onBack = {
                    handleEvent(FriendOptionEvent.GoBack)
                }
            )
        },
    ) { padding ->
        Image(
            painter = painterResource(R.drawable.bg_with_circle),
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyPoint(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp)),
                memberInfo = memberInfo,
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
            MarginVertical(20)
            ButtonRadius(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                title = stringResource(id = R.string.add_friend_chat_now).uppercase(),
                bgColor = PictonBlue,
                icon = painterResource(id = R.drawable.ic_chat)
            ) {
                handleEvent(FriendOptionEvent.GoToChatScreen)
            }
            MarginVertical(16)
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

                }
                MarginHorizontal(12)
                ButtonRadius(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    title = stringResource(id = R.string.add_friend_send_point).uppercase(),
                    bgColor = PrimaryGreen,
                    textColor = Color.Black
                ) {

                }
            }
            Expanded()
            (context as? Activity)?.let {
                DynamicNative().render(
                    DefaultMediumNative2(context = it),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(
                            min = 100.dp,
                            max = 200.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun MyPoint(
    memberInfo: MemberInfo,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painterResource(R.drawable.wallet_point_card),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Expanded()
                Text(
                    stringResource(R.string.my_point),
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wallet_ic_point),
                        contentDescription = null,
                        modifier = Modifier.size(37.dp)
                    )
                    SizeBox(width = 10.dp)
                    Text(
                        text = memberInfo.point.formatDigitsNumber(),
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Orange300
                        )
                    )
                }
                Expanded()
            }
        }
    }
}
