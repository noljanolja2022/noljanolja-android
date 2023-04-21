package com.noljanolja.android.features.home.wallet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.dashedBorder
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
        val friendNumber = uiState.data.friendNumber
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            UserInformation(user = user, friendNumber = friendNumber)
            UserPoint()
            UserWalletInfo()
            Expanded()
            PrimaryButton(
                text = stringResource(id = R.string.common_log_out).uppercase(),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    handleEvent(WalletEvent.Logout)
                }
            )
            SizeBox(height = 24.dp)
        }
    }
}

@Composable
private fun UserInformation(user: User, friendNumber: Int) {
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
            Row(
                modifier = Modifier.height(26.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(id = R.drawable.ic_king), contentDescription = null)
                SizeBox(width = 4.dp)
                Text(
                    text = stringResource(id = R.string.wallet_gold_member_ship),
                    style = TextStyle(
                        fontSize = 8.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Medium
                    ),
                )
            }
            Text(
                text = stringResource(id = R.string.wallet_number_friends, friendNumber),
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Expanded()
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = null)
        }
    }
}

@Composable
private fun UserPoint() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .height(116.dp)
                .dashedBorder(
                    strokeWidth = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    cornerRadiusDp = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.wallet_my_point),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.ic_coin), contentDescription = null)
                SizeBox(width = 8.dp)
                Text(
                    text = "982,350",
                    style = TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}

@Composable
private fun UserWalletInfo() {
    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)) {
        WalletInfoItem(
            R.drawable.img_coins,
            "Accumulated points for the day",
            14_000,
            "View History",
            {}
        )
        SizeBox(width = 12.dp)
        WalletInfoItem(
            R.drawable.img_exchange,
            "Points that can be exchanged",
            8_500,
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
    textButton: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1F)
            .height(246.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(75.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = "$value P",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 35.sp,
            )
        )
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(34.dp),
            shape = RoundedCornerShape(5.dp),
            elevation = ButtonDefaults.buttonElevation(),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(textButton.uppercase(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}