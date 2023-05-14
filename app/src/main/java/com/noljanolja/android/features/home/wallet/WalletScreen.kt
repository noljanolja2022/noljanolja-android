package com.noljanolja.android.features.home.wallet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalContext
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
import com.noljanolja.android.features.home.wallet.composable.TierIcon
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange400
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.android.util.getBackgroundColor
import com.noljanolja.android.util.getTitle
import com.noljanolja.android.util.orZero
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.loyalty.domain.model.MemberTier
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WalletContent(uiState = uiState, handleEvent = viewModel::handleEvent)
}

@Composable
private fun WalletContent(
    uiState: UiState<WalletUIData>,
    handleEvent: (WalletEvent) -> Unit,
) {
    ScaffoldWithUiState(uiState = uiState) {
        val user = uiState.data?.user ?: return@ScaffoldWithUiState
        val memberInfo = uiState.data.memberInfo
        val friendNumber = uiState.data.friendNumber
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ) {
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
            UserPoint(memberInfo?.point.orZero().formatDigitsNumber())
            UserWalletInfo(
                onGoToTransactionHistory = {
                    handleEvent(WalletEvent.TransactionHistory)
                }
            )
            UserAttendance()
            Expanded()
            SizeBox(height = 24.dp)
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
        modifier = Modifier.fillMaxWidth().clip(
            RoundedCornerShape(
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ).background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 24.dp)
    ) {
        CircleAvatar(user = user, size = 64.dp)
        SizeBox(width = 16.dp)
        Column(
            verticalArrangement = Arrangement.SpaceBetween
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
            Text(
                text = stringResource(id = R.string.wallet_point_ranking, 12345.formatDigitsNumber()),
                style = MaterialTheme.typography.labelSmall,
            )
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
private fun UserPoint(
    point: String,
) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(79.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.wallet_my_point),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_coin),
                    contentDescription = null,
                    modifier = Modifier.size(37.dp)
                )
                SizeBox(width = 10.dp)
                Text(
                    text = point,
                    style = TextStyle(
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange400
                    )
                )
            }
        }
    }
}

@Composable
private fun UserWalletInfo(
    onGoToTransactionHistory: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        WalletInfoItem(
            R.drawable.img_coins,
            "Accumulated points for the day",
            14_000,
            valueColor = Color(0xFF623B00),
            "View History",
            onGoToTransactionHistory,
        )
        SizeBox(width = 12.dp)
        WalletInfoItem(
            R.drawable.img_exchange,
            "Points that can be exchanged",
            8_500,
            valueColor = Color(0xFF007AFF),
            "Exchange money",
            {}
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
        modifier = Modifier.weight(1F).wrapContentHeight().clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.background).padding(horizontal = 8.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.height(50.dp)
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
            modifier = Modifier.fillMaxWidth().height(34.dp),
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
                modifier = Modifier.fillMaxWidth()
            ) {
                SizeBox(height = 17.dp)
                Box(modifier = Modifier.padding(start = 24.dp, end = 12.dp).fillMaxWidth().wrapContentHeight()) {
                    Image(
                        painter = painterResource(id = R.drawable.attendance_banner),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
                SizeBox(height = 18.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(end = 12.dp).weight(1F)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "My attendance",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            SizeBox(width = 10.dp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "12",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
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
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).height(6.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surface,
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(start = 18.dp).weight(1F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F6D00)
                        ),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            "Attend now",
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
            modifier = Modifier.padding(start = 10.dp).size(88.dp).align(Alignment.TopStart)
        )
    }
}

@Composable
private fun RankingRow(tier: MemberTier, onClick: () -> Unit) {
    val containerColor: Color = tier.getBackgroundColor()
    val contentColor: Color
    val context = LocalContext.current
    with(MaterialTheme.colorScheme) {
        when (tier) {
            MemberTier.BRONZE -> {
                contentColor = onBackground
            }

            MemberTier.SILVER -> {
                contentColor = background
            }

            MemberTier.GOLD -> {
                contentColor = background
            }

            MemberTier.PREMIUM -> {
                contentColor = background
            }
        }
    }
    Row(
        modifier = Modifier.height(26.dp).clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TierIcon(tier = tier, width = 20.dp)
        SizeBox(width = 4.dp)
        Text(
            text = tier.getTitle(context),
            style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = contentColor,
                fontWeight = FontWeight.Medium
            ),
        )
    }
}