package com.noljanolja.android.features.home.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.farimarwat.composenativeadmob.nativead.rememberNativeAdState
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.wallet.composable.WalletUserInformation
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithCircleBgRoundedContent
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.admob.AdmobRectangle
import com.noljanolja.android.ui.theme.BlueMain
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun WalletExchangeScreen(
    viewModel: WalletViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    WalletExchangeContent(
        uiState = uiState,
        memberInfo = memberInfo,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun WalletExchangeContent(
    uiState: UiState<WalletUIData>,
    memberInfo: MemberInfo,
    handleEvent: (WalletEvent) -> Unit,
) {
    var showAdmob by remember { mutableStateOf(false) }
    ScaffoldWithCircleBgRoundedContent(
        heading = {
            WalletUserInformation(
                user = uiState.data?.user ?: User(),
                memberInfo = memberInfo,
                goToRanking = {
                    handleEvent(WalletEvent.Ranking)
                },
                goToSetting = {
                    handleEvent(WalletEvent.Setting)
                },
                modifier = Modifier.padding(top = 26.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            SizeBox(height = 16.dp)
            MyPoint(memberInfo)
            SizeBox(height = 10.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                WalletInfoDailyInfoItem(
                    modifier = Modifier.weight(1f),
                    background = R.drawable.bg_accumulated,
                    contentColor = NeutralLight,
                    title = R.string.wallet_accumulated_point,
                    point = memberInfo.accumulatedPointsToday,
                    pointColor = MaterialTheme.colorScheme.secondary
                )
                SizeBox(width = 10.dp)
                WalletInfoDailyInfoItem(
                    modifier = Modifier.weight(1f),
                    background = R.drawable.bg_point,
                    contentColor = NeutralDarkGrey,
                    title = R.string.wallet_point_can_exchange,
                    point = memberInfo.exchangeablePoints,
                    pointColor = BlueMain
                )
            }
            SizeBox(height = 10.dp)
            Card(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.convert_points_to_cash),
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.titleSmall.withMedium()
                        )
                        Icon(
                            Icons.Default.Help,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                    SizeBox(height = 10.dp)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.wallet_ic_point),
                            contentDescription = null,
                            modifier = Modifier.size(37.dp)
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 27.dp).size(37.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.wallet_ic_coin),
                            contentDescription = null,
                            modifier = Modifier.size(37.dp)
                        )
                    }
                    SizeBox(height = 14.dp)
                    PrimaryButton(
                        text = stringResource(R.string.convert_now),
                        containerColor = Orange300,
                        contentColor = NeutralDarkGrey,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        showAdmob = true
                    }
                }
            }
        }
    }

    if (showAdmob) {
        AdmobDialog(
            onClose = {
                showAdmob = false
                handleEvent(WalletEvent.Exchange)
            }
        )
    }
}

@Composable
private fun MyPoint(
    memberInfo: MemberInfo,
) {
    val isDarkMode = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.wallet_cash_card),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
            Image(
                painter = painterResource(
                    if (isDarkMode) {
                        R.drawable.wallet_point_card_dark
                    } else {
                        R.drawable.wallet_point_card
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Expanded()
                Text(
                    stringResource(R.string.my_point),
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                SizeBox(height = 10.dp)
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

@Composable
fun WalletInfoDailyInfoItem(
    modifier: Modifier = Modifier,
    background: Int,
    title: Int,
    point: Long,
    contentColor: Color,
    pointColor: Color,
) {
    Card(
        modifier = modifier.aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box {
            Image(
                painter = painterResource(background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
            ) {
                Text(
                    stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
                SizeBox(height = 10.dp)
                Row {
                    Text(
                        text = point.formatDigitsNumber(),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 35.sp,
                            color = contentColor
                        )
                    )
                    Text(
                        " P",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 35.sp,
                            color = pointColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AdmobDialog(
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        val context = LocalContext.current

        Box {
            AdmobRectangle(modifier = Modifier.fillMaxWidth().aspectRatio(1f))
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        onClose()
                    }
            )
        }
    }
}