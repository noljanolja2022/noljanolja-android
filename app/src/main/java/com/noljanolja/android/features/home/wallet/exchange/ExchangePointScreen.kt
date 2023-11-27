package com.noljanolja.android.features.home.wallet.exchange

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.noljanolja.android.MyApplication
import com.noljanolja.android.R
import com.noljanolja.android.common.ads.interstitial.AdmobInterstitial
import com.noljanolja.android.common.ads.nativeads.DefaultMediumNative2
import com.noljanolja.android.common.ads.nativeads.DynamicNative
import com.noljanolja.android.common.sharedpreference.SharedPreferenceHelper
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.BlueMain
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.NeutralDeepGrey
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.showToast
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.exchange.domain.domain.ExchangeRate
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun ExchangePointScreen(
    viewModel: ExchangePointViewModel = getViewModel(),
) {
    val memberInfo by viewModel.memberInfoFlow.collectAsStateWithLifecycle()
    val myBalance by viewModel.myBalanceFlow.collectAsStateWithLifecycle()
    val exchangeRate by viewModel.exchangeRateFlow.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<Throwable?>(null) }
    ExchangePointContent(
        memberInfo = memberInfo,
        myBalance = myBalance,
        exchangeRate = exchangeRate,
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
    exchangeRate: ExchangeRate,
    handleEvent: (ExchangeEvent) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferenceHelper: SharedPreferenceHelper = get()
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
                .verticalScroll(rememberScrollState())
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
                        scope.launch {
                            convertPoint(
                                context = context,
                                rewardRecurringAmount = exchangeRate.rewardRecurringAmount,
                                sharedPreferenceHelper = sharedPreferenceHelper,
                                onConvert = {
                                    handleEvent(ExchangeEvent.Convert)
                                }
                            )
                        }
                    },
                    isEnable = memberInfo.point > 0
                )
            }
            SizeBox(height = 50.dp)
            Expanded()
            (context as? Activity)?.let {
                DynamicNative().render(
                    DefaultMediumNative2(context = it),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
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
                memberInfo.point.formatDigitsNumber(),
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
                        text = myBalance.balance.toInt().toString(),
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

@Composable
private fun BottomNativeAd(
    nativeAd: NativeAd,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(10.dp)
            .clickable {
                if (nativeAd.callToAction == null) {
                    nativeAd.performClick(nativeAd.extras)
                }
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                ImageRequest.Builder(context = context)
                    .data(nativeAd.icon?.uri)
                    .placeholder(R.drawable.placeholder_account)
                    .error(R.drawable.placeholder_account)
                    .fallback(R.drawable.placeholder_account)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp),
                contentScale = ContentScale.FillBounds,
            )
            SizeBox(width = 8.dp)
            Column {
                Text(
                    nativeAd.headline.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium.withMedium(),
                    color = BlueMain
                )
                SizeBox(height = 5.dp)
                Text(
                    nativeAd.body.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralDeepGrey
                )
                SizeBox(height = 8.dp)
            }
        }
        nativeAd.callToAction?.let {
            PrimaryButton(text = it) {
                nativeAd.performClick(nativeAd.extras)
            }
        }
    }
}

private suspend fun convertPoint(
    context: Context,
    rewardRecurringAmount: Int,
    sharedPreferenceHelper: SharedPreferenceHelper,
    onConvert: () -> Unit,
) {
    if (rewardRecurringAmount == 0 || (sharedPreferenceHelper.convertPointCount + 1) % rewardRecurringAmount != 0) {
        onConvert()
    } else {
        MyApplication.clearAllPipActivities()
        delay(50)
        (context as? Activity)?.let {
            AdmobInterstitial().show(
                activity = it,
                enableDialog = false,
                onCompleted = {
                    if (it == true) {
                        onConvert()
                    } else {
                        context.showToast(context.getString(R.string.load_ads_fail))
                    }
                }
            )
        }
    }
    sharedPreferenceHelper.convertPointCount++
}