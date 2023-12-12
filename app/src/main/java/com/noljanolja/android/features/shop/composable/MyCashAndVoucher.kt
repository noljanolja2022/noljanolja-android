package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance

@Composable
fun MyCashAndVoucher(
    myBalance: ExchangeBalance,
    onViewCoupons: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        MyCash(
            myBalance = myBalance,
            modifier = Modifier.weight(1f)
        )
        SizeBox(width = 16.dp)
        MyVouchers(
            giftCount = myBalance.giftCount,
            modifier = Modifier.weight(1f).clickable {
                onViewCoupons()
            }
        )
    }
}

@Composable
private fun MyCash(
    myBalance: ExchangeBalance,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 19.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.my_cash),
                style = MaterialTheme.typography.bodyLarge.withBold(),
                color = Orange300,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
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
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

@Composable
private fun MyVouchers(
    giftCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 19.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.shop_coupon),
                style = MaterialTheme.typography.bodyLarge.withBold(),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_wallet_sharp),
                    contentDescription = null,
                    modifier = Modifier.size(37.dp)
                )
                SizeBox(width = 10.dp)
                Text(
                    text = "$giftCount +",
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}