package com.noljanolja.android.features.home.wallet.exchange

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.admob.AdmobLargeBanner
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import org.koin.androidx.compose.getViewModel

@Composable
fun ExchangePointScreen(
    viewModel: ExchangePointViewModel = getViewModel(),
) {
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    val myBalance by viewModel.myBalanceFlow.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<Throwable?>(null) }
    ExchangePointContent(
        memberInfo = memberInfo,
        myBalance = myBalance,
        handleEvent = viewModel::handleEvent
    )

    ErrorDialog(
        showError = error != null,
        title = stringResource(R.string.common_error_title),
        description = error?.message.orEmpty(),
        onDismiss = { error = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangePointContent(
    memberInfo: MemberInfo,
    myBalance: ExchangeBalance,
    handleEvent: (ExchangeEvent) -> Unit,
) {
    Box {
        Image(
            painterResource(R.drawable.bg_with_circle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
    Scaffold(
        topBar = {
            CommonTopAppBar(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = stringResource(R.string.convert_coins),
                onBack = {
                    handleEvent(ExchangeEvent.Back)
                },
                centeredTitle = true
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            MyCash(memberInfo = memberInfo, myBalance = myBalance)
            SizeBox(height = 10.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.tab_convert_coin_to_cash),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                SizeBox(height = 10.dp)
                Text(
                    text = stringResource(R.string.tab_conver_coin_description),
                    // Android/Body/Medium/Regular
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.secondaryTextColor(),
                    textAlign = TextAlign.Center
                )
                SizeBox(height = 25.dp)
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Orange300,
                    text = stringResource(R.string.convert),
                    onClick = {
                        handleEvent(ExchangeEvent.Convert)
                    },
                    isEnable = memberInfo.point > 0
                )
            }
            Expanded()
            AdmobLargeBanner(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun MyCash(myBalance: ExchangeBalance, memberInfo: MemberInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NeutralDarkGrey)
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                stringResource(R.string.my_point),
                style = MaterialTheme.typography.bodyMedium.withBold(),
                color = NeutralLight
            )
            SizeBox(width = 10.dp)
            Text(
                memberInfo.point.toString(),
                style = MaterialTheme.typography.bodyMedium.withBold(),
                color = Orange300
            )
        }
        SizeBox(height = 10.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Image(
                painterResource(R.drawable.wallet_cash_card),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Expanded()
                Text(
                    stringResource(R.string.my_cash),
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = NeutralDarkGrey
                )
                SizeBox(height = 10.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wallet_ic_coin),
                        contentDescription = null,
                        modifier = Modifier.size(37.dp)
                    )
                    SizeBox(width = 10.dp)
                    Text(
                        text = myBalance.balance.toString(),
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDarkGrey
                        )
                    )
                }
                Expanded()
            }
        }
    }
}