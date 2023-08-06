package com.noljanolja.android.features.home.wallet.checkin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.noljanolja.android.features.home.CheckinEvent
import com.noljanolja.android.features.home.CheckinViewModel
import com.noljanolja.android.features.home.wallet.AttendeeInformationItem
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.InfoDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.getDayOfWeek
import com.noljanolja.android.util.isBeforeDate
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.toInstant
import com.noljanolja.core.user.domain.model.CheckinProgress
import com.patrykandpatrick.vico.core.extension.orZero
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock

@Composable
fun CheckinScreen(
    viewModel: CheckinViewModel,
) {
    val context = LocalContext.current
    val checkinProgresses by viewModel.checkinProgressFlow.collectAsStateWithLifecycle()
    var checkinSuccessMessage by remember {
        mutableStateOf("")
    }
    LaunchedEffect(viewModel.checkinSuccessEvent) {
        viewModel.checkinSuccessEvent.collectLatest {
            checkinSuccessMessage = it
        }
    }
    CheckinContent(checkinProgresses = checkinProgresses, handleEvent = viewModel::handleEvent)
    InfoDialog(
        title = {
            Text(
                stringResource(id = R.string.common_success),
                style = MaterialTheme.typography.titleLarge
            )
        },
        content = checkinSuccessMessage,
        dismissText = stringResource(
            id = R.string.common_ok
        ),
        isShown = checkinSuccessMessage.isNotBlank(),
        onDismiss = { checkinSuccessMessage = "" }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckinContent(
    checkinProgresses: List<CheckinProgress>,
    handleEvent: (CheckinEvent) -> Unit,
) {
    Scaffold(topBar = {
        CommonTopAppBar(
            title = stringResource(id = R.string.checkout_and_play),
            onBack = { handleEvent(CheckinEvent.Back) },
            centeredTitle = true,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }) { padding ->
        val checkedDay = checkinProgresses.filter { it.rewardPoints > 0 }.size

        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SizeBox(height = 16.dp)
                Column(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(id = R.string.wallet_my_attendance),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.background
                        )
                        SizeBox(width = 8.dp)
                        Text(
                            checkedDay.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background
                            )
                        )
                        Text(
                            " / ${checkinProgresses.size}",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                    SizeBox(height = 4.dp)
                    LinearProgressIndicator(
                        progress = (checkedDay.toFloat() / checkinProgresses.size).takeIf { checkinProgresses.isNotEmpty() }.orZero,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).fillMaxWidth()
                            .height(6.dp),
                        color = Orange300,
                        trackColor = MaterialTheme.colorScheme.surface,
                    )
                }
                SizeBox(height = 20.dp)
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp)
                        .fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AttendeeInformationItem(
                        modifier = Modifier.weight(1f).padding(18.dp),
                        firstText = stringResource(id = R.string.wallet_to_get),
                        secondText = stringResource(id = R.string.wallet_benefit).uppercase()
                    )
                    AttendeeInformationItem(
                        modifier = Modifier.weight(1f).padding(vertical = 18.dp)
                            .padding(end = 18.dp),
                        firstText = stringResource(id = R.string.wallet_checkin),
                        secondText = stringResource(id = R.string.wallet_every_day).uppercase()
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(26.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
                PrimaryButton(
                    text = stringResource(id = R.string.invite_to_get_benefits),
                    modifier = Modifier.padding(horizontal = 16.dp).height(52.dp)
                ) {
                    handleEvent(CheckinEvent.Referral)
                }
            }
            CheckinCalendarContent(checkinProgresses = checkinProgresses)
            SizeBox(height = 25.dp)
            Box(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(id = R.string.wallet_checkin),
                modifier = Modifier.padding(horizontal = 16.dp).height(52.dp)
            ) {
                handleEvent(CheckinEvent.Checkin)
            }
            SizeBox(height = 16.dp)
        }
    }
}

@Composable
private fun CheckinCalendarContent(
    checkinProgresses: List<CheckinProgress>,
) {
    val row = (checkinProgresses.size + 6) / 7
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 12.dp)
    ) {
        repeat(7) {
            Text(
                text = if (it == 6) "Sun" else (it + 2).toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.withMedium(),
                color = MaterialTheme.secondaryTextColor()
            )
        }
    }

    val startIndex =
        (checkinProgresses.firstOrNull()?.day?.toInstant()?.getDayOfWeek()?.value ?: 1) - 1
    repeat(row) { rowIndex ->
        SizeBox(height = 8.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            repeat(7) {
                val index = it - startIndex
                val progress = checkinProgresses.getOrNull(rowIndex * 7 + index)
                progress?.let {
                    val isInPass = it.day.toInstant().isBeforeDate(Clock.System.now())
                    if (it.rewardPoints > 0) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.clip(CircleShape).weight(1f).aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(10.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Image(
                            painter = painterResource(id = if (isInPass) R.drawable.ic_point else R.drawable.ic_coin),
                            contentDescription = null,
                            modifier = Modifier.clip(CircleShape).weight(1f).border(
                                width = 1.5.dp,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.outline
                            ).aspectRatio(1f).padding(10.dp),
                        )
                    }
                } ?: Box(modifier = Modifier.weight(1f))
                if (index < 6) {
                    SizeBox(width = 4.dp)
                }
            }
        }
    }
}