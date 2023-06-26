package com.noljanolja.android.features.home.wallet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.RankingRow
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.UserPoint
import com.noljanolja.android.ui.theme.darkContent
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = getViewModel(),
    onUseNow: () -> Unit,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    WalletContent(
        uiState = uiState,
        memberInfo = memberInfo,
        handleEvent = viewModel::handleEvent,
        onUseNow = onUseNow
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WalletContent(
    uiState: UiState<WalletUIData>,
    memberInfo: MemberInfo,
    handleEvent: (WalletEvent) -> Unit,
    onUseNow: () -> Unit,
) {
    val state = rememberPullRefreshState(uiState.loading, { handleEvent(WalletEvent.Refresh) })
    ScaffoldWithUiState(uiState = uiState) {
        val user = uiState.data?.user ?: return@ScaffoldWithUiState
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .pullRefresh(state)
        ) {
            item {
                UserInformation(
                    user = user,
                    memberInfo = memberInfo,
                    goToSetting = {
                        handleEvent(WalletEvent.Setting)
                    },
                    goToRanking = {
                        handleEvent(WalletEvent.Ranking)
                    }
                )
            }
            item {
                UserPoint(memberInfo.point.formatDigitsNumber(), modifier = Modifier.padding(16.dp))
            }
            item {
                UserWalletInfo(
                    memberInfo = memberInfo,
                    onGoToTransactionHistory = {
                        handleEvent(WalletEvent.TransactionHistory)
                    },
                    onUseNow = onUseNow
                )
            }
            item {
                UserAttendance()
            }
            item {
                SizeBox(height = 24.dp)
            }
        }
    }
}

@Composable
private fun UserInformation(
    user: User,
    memberInfo: MemberInfo?,
    goToSetting: () -> Unit,
    goToRanking: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar(user = user, size = 64.dp)
        SizeBox(width = 16.dp)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight(),

        ) {
            Text(
                text = user.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            memberInfo?.currentTier?.let { RankingRow(tier = it, onClick = goToRanking) }
//            Text(
//                text = stringResource(id = R.string.wallet_point_ranking, 12345.formatDigitsNumber()),
//                style = MaterialTheme.typography.labelSmall,
//            )
        }
        Expanded()
        IconButton(
            onClick = goToSetting,
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = null)
        }
    }
}

@Composable
private fun UserWalletInfo(
    memberInfo: MemberInfo,
    onGoToTransactionHistory: () -> Unit,
    onUseNow: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
    ) {
        WalletInfoItem(
            R.drawable.img_coins,
            stringResource(id = R.string.wallet_accumulated_point),
            memberInfo.accumulatedPointsToday,
            valueColor = Color(0xFF623B00),
            stringResource(id = R.string.wallet_view_history),
            onGoToTransactionHistory,
        )
        SizeBox(width = 12.dp)
        WalletInfoItem(
            R.drawable.img_exchange,
            stringResource(id = R.string.wallet_point_can_exchange),
            memberInfo.exchangeablePoints,
            valueColor = Color(0xFF007AFF),
            stringResource(id = R.string.wallet_exchange_money),
            onClick = onUseNow
        )
    }
}

@Composable
private fun RowScope.WalletInfoItem(
    @DrawableRes image: Int,
    description: String,
    value: Long,
    valueColor: Color,
    textButton: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1F)
            .wrapContentHeight()
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.heightIn(min = 50.dp)
        )
        Row {
            Text(
                text = value.formatDigitsNumber(),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 35.sp,
                    color = valueColor
                )
            )
            Text(
                " P",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 35.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        SizeBox(height = 14.dp)
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            shape = RoundedCornerShape(5.dp),
            elevation = ButtonDefaults.buttonElevation(),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(textButton.uppercase(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun UserAttendance() {
    Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
        Card(
            modifier = Modifier.padding(top = 7.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                SizeBox(height = 17.dp)
                Box(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 12.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.attendance_banner),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
                SizeBox(height = 18.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .weight(1F)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(id = R.string.wallet_my_attendance),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.darkContent()
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "12",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.darkContent()
                                    )
                                )
                                Text(
                                    " / 30",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        SizeBox(height = 4.dp)
                        LinearProgressIndicator(
                            progress = 12f / 30,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surface,
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(start = 18.dp)
                            .weight(1F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F6D00)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            stringResource(id = R.string.wallet_attend_now),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
                SizeBox(height = 14.dp)
            }
        }
        Image(
            painterResource(id = R.drawable.ic_light_wallet),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(88.dp)
                .align(Alignment.TopStart)
        )
    }
}