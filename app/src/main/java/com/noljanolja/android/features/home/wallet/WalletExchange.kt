package com.noljanolja.android.features.home.wallet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.lifecycle.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.home.wallet.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.composable.admob.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.loyalty.domain.model.*
import com.noljanolja.core.user.domain.model.*
import org.koin.androidx.compose.*

@Composable
fun WalletExchangeScreen(
    onNavigateToShop: () -> Unit,
    viewModel: WalletViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    val myBalance by viewModel.myBalanceFlow.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.handleEvent(WalletEvent.Refresh)
    }
    WalletExchangeContent(
        uiState = uiState,
        memberInfo = memberInfo,
        myBalance = myBalance,
        handleEvent = viewModel::handleEvent,
        onWalletPointClick = onNavigateToShop
    )
}

@Composable
private fun WalletExchangeContent(
    uiState: UiState<WalletUIData>,
    memberInfo: MemberInfo,
    myBalance: ExchangeBalance,
    handleEvent: (WalletEvent) -> Unit,
    onWalletPointClick: () -> Unit
) {
    val context = LocalContext.current
    var showAdmob by remember { mutableStateOf(false) }
    val sharedPreferenceHelper: SharedPreferenceHelper = get()
    var pointOfDayLineCount by remember {
        mutableStateOf(0)
    }
    var pointLineCount by remember {
        mutableStateOf(0)
    }
    var titlePointOfDay by remember {
        mutableStateOf(context.getString(R.string.wallet_accumulated_point))
    }
    var titlePoint by remember {
        mutableStateOf(context.getString(R.string.wallet_point_can_exchange))
    }
    ScaffoldWithCircleAboveBgContent(
        backgroundColor = MaterialTheme.colorBackgroundWallet(
            key = sharedPreferenceHelper.appColor
        ),
        backgroundAboveColor = MaterialTheme.colorBackgroundWalletAbove(
            key = sharedPreferenceHelper.appColor
        ),
        heading = {
            Column {
                WalletUserInformation(
                    user = uiState.data?.user ?: User(),
                    memberInfo = memberInfo,
                    goToRanking = {
                        handleEvent(WalletEvent.Ranking)
                    },
                    goToSetting = {
                        handleEvent(WalletEvent.Setting)
                    },
                    modifier = Modifier.padding(
                        top = 26.dp,
                        bottom = 10.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )
                MyCashAndPoint(
                    memberInfo = memberInfo,
                    myBalance = myBalance,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SizeBox(height = 10.dp)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                WalletInfoDailyInfoItem(
                    modifier = Modifier.weight(1f),
                    contentColor = Orange00,
                    title = titlePointOfDay,
                    point = memberInfo.accumulatedPointsToday,
                    pointColor = MaterialTheme.colorScheme.onBackground,
                    onTextLayout = {
                        if (pointOfDayLineCount != it) {
                            pointOfDayLineCount = it
                            titlePointOfDay = titlePointOfDay.addLine(
                                textLine = pointOfDayLineCount,
                                anotherLine = pointLineCount
                            )
                        }
                    }
                )
                SizeBox(width = 12.dp)
                WalletInfoDailyInfoItem(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onWalletPointClick()
                        },
                    contentColor = BlueMain,
                    title = titlePoint,
                    point = memberInfo.exchangeablePoints,
                    pointColor = MaterialTheme.colorScheme.onBackground,
                    onTextLayout = {
                        if (pointLineCount != it) {
                            pointLineCount = it
                            titlePoint = titlePoint.addLine(
                                textLine = pointLineCount,
                                anotherLine = pointOfDayLineCount
                            )
                        }
                    }
                )
            }
            SizeBox(height = 20.dp)
            ButtonRadius(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 0.1.dp,
                        shape = RoundedCornerShape(5.dp),
                        color = MaterialTheme.colorBackgroundWallet(
                            key = sharedPreferenceHelper.appColor
                        )
                    ),
                title = stringResource(id = R.string.transaction_history).uppercase(),
                bgColor = MaterialTheme.colorScheme.primary,
                textColor = Color.Black,
                icon = painterResource(id = R.drawable.ic_history)
            ) {
                handleEvent(WalletEvent.TransactionHistory)
            }
            SizeBox(height = 20.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = Orange300
                    ),
                colors = CardDefaults.cardColors(containerColor = Yellow00),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.convert_points_to_cash),
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.titleSmall.withMedium(),
                            color = Color.Black
                        )
                        Icon(
                            Icons.Default.Help,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.TopEnd),
                            tint = Color.Black
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
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 27.dp)
                                .size(37.dp)
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
                        handleEvent(WalletEvent.Exchange)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.wallet_cash_card),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
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
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
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
    title: String,
    point: Long,
    contentColor: Color,
    pointColor: Color,
    onTextLayout: (Int) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = pointColor,
                textAlign = TextAlign.Center,
                maxLines = 4,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    onTextLayout(textLayoutResult.lineCount)
                }
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

@Composable
fun AdmobDialog(
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box {
            AdmobRectangle(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
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