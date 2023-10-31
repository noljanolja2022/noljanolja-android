package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import kotlin.math.abs

@Composable
fun MyCashAndPoint(
    memberInfo: MemberInfo,
    myBalance: ExchangeBalance,
) {
    var card1Visible by remember { mutableStateOf(true) }
    val transition = updateTransition(targetState = card1Visible, label = "cardTransition")

    val card1OffsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 0f else 24f
    }

    val card2OffsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 24f else 0f
    }

    val card1ZIndex by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 0f else 1f
    }

    val card2ZIndex by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 1f else 0f
    }

    val card1Scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 0.9f else 1f
    }

    val card2Scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 500)
            } else {
                tween(durationMillis = 500)
            }
        },
        label = ""
    ) { isVisible ->
        if (isVisible) 1f else 0.9f
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        MyPoint(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .offset(y = card1OffsetY.dp)
                .zIndex(card1ZIndex)
                .scale(scale = card1Scale)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (abs(dragAmount) > 20) {
                            card1Visible = !card1Visible
                        }
                    }
                }
                .clickable { card1Visible = !card1Visible },
            memberInfo = memberInfo,
        )
        MyCash(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .offset(y = card2OffsetY.dp)
                .zIndex(card2ZIndex)
                .scale(scale = card2Scale)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (abs(dragAmount) > 20) {
                            card1Visible = !card1Visible
                        }
                    }
                }
                .clickable {
                    card1Visible = !card1Visible
                },
            myBalance = myBalance
        )
    }
}

@Composable
private fun MyPoint(
    modifier: Modifier = Modifier,
    memberInfo: MemberInfo,
) {
    val isDarkMode = isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box {
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
                modifier = Modifier.fillMaxWidth()
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
fun MyCash(
    modifier: Modifier = Modifier,
    myBalance: ExchangeBalance,
) {
    Card(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
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