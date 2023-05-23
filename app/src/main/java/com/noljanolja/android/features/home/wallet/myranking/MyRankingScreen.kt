package com.noljanolja.android.features.home.wallet.myranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.wallet.composable.TierIcon
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.getBackgroundColor
import com.noljanolja.android.util.getTitle
import com.noljanolja.core.loyalty.domain.model.MemberTier
import org.koin.androidx.compose.getViewModel

@Composable
fun MyRankingScreen(
    viewModel: MyRankingViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    MyRankingContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun MyRankingContent(
    uiState: UiState<MyRankingUiData>,
    handleEvent: (MyRankingEvent) -> Unit,
) {
    val context = LocalContext.current
    ScaffoldWithUiState(uiState = uiState, topBar = {
        CommonTopAppBar(
            title = stringResource(id = R.string.my_ranking_title),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            centeredTitle = true,
            onBack = { handleEvent(MyRankingEvent.Back) }
        )
    }) {
        val memberInfo = uiState.data?.memberInfo ?: return@ScaffoldWithUiState
        LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)
                        .background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MyRankingInfo(tier = memberInfo.currentTier)
//                    SizeBox(height = 3.dp)
//                    Row() {
//                        Text(
//                            "Overall Point Ranking: ",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        Text(
//                            text = 12345.formatDigitsNumber(),
//                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
//                            color = MaterialTheme.colorScheme.primaryContainer
//                        )
//                    }
                    memberInfo.nextTier?.let { nextTier ->
                        SizeBox(height = 8.dp)
                        Text(
                            text = stringResource(id = R.string.wallet_expect_next_tier),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SizeBox(height = 6.dp)
                        TierIcon(tier = nextTier)
                        SizeBox(height = 6.dp)
                        Text(
                            text = nextTier.getTitle(context),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(nextTier.getBackgroundColor()).padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }
                }
            }
            MemberTier.values().forEach {
                item {
                    RankingInfo(tier = it)
                }
            }
            item {
                SizeBox(height = 10.dp)
            }
        }
    }
}

@Composable
private fun MyRankingInfo(tier: MemberTier) {
    val context = LocalContext.current
    TierIcon(
        tier = tier,
        width = 45.dp
    )
    SizeBox(height = 3.dp)
    Text(tier.getTitle(context), style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun RankingInfo(tier: MemberTier) {
    val context = LocalContext.current
    val backgroundColor = tier.getBackgroundColor()
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background).padding(vertical = 10.dp, horizontal = 36.dp),
        ) {
            Box(
                modifier = Modifier.size(33.dp).clip(RoundedCornerShape(5.dp)).background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                TierIcon(tier = tier, width = 24.dp)
            }
            SizeBox(width = 16.dp)
            Column() {
                Text(
                    text = tier.getTitle(context),
                    style = MaterialTheme.typography.titleMedium,
                )
//                Text(
//                    text = tier.getDescription(context),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.secondaryTextColor()
//                )
            }
        }
    }
}