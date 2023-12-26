package com.noljanolja.android.features.setting.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.darkContent
import com.noljanolja.android.ui.theme.withBold
import org.koin.androidx.compose.getViewModel

@Composable
fun FAQScreen(viewModel: AppInfoViewModel = getViewModel()) {
    FAQContent(
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FAQContent(
    handleEvent: (AppInfoEvent) -> Unit,
) {
    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    val qAs by remember {
        mutableStateOf(
            listOf(
                QA(
                    "What is NolgoBulja?",
                    "NolgoBulja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "What are Benefits?",
                    "NolgoBulja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "How can I make purchase?",
                    "NolgoBulja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "How can I get more points?",
                    "NolgoBulja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "How to delete account?",
                    "NolgoBulja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
            )
        )
    }
    Scaffold(topBar = {
        CommonTopAppBar(
            centeredTitle = true,
            title = stringResource(id = R.string.setting_faq_title),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onBack = {
                handleEvent(AppInfoEvent.Back)
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                SizeBox(height = 45.dp)
            }
            items(qAs.size) { index ->
                val qa = qAs[index]
                if (index != selectedIndex) {
                    QAItem(qa = qa, onClick = {
                        selectedIndex = index
                    })
                } else {
                    SelectedQAItem(qa = qa, onClick = {
                        selectedIndex = -1
                    })
                }
            }
        }
    }
}

@Composable
private fun QAItem(qa: QA, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                onClick.invoke()
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                qa.question,
                style = MaterialTheme
                    .typography
                    .bodyLarge
                    .withBold()
                    .copy(color = MaterialTheme.colorScheme.secondary)
            )
            IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
        Divider()
    }
}

@Composable
private fun SelectedQAItem(qa: QA, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .background(Color(0xFFFFFAD0))
            .padding(vertical = 30.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                qa.question,
                style = MaterialTheme
                    .typography
                    .bodyLarge
                    .withBold()
                    .copy(color = MaterialTheme.colorScheme.secondary)
            )
            IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
        SizeBox(height = 16.dp)
        Text(
            text = qa.answer,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.darkContent()
        )
    }
}

private data class QA(
    val question: String,
    val answer: String,
)