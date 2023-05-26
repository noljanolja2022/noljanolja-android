package com.noljanolja.android.features.setting.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withBold

@Composable
fun FAQScreen() {
    FAQContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FAQContent() {
    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    val qAs by remember {
        mutableStateOf(
            listOf(
                QA(
                    "What is Nolja Nolja?",
                    "Nolja Nolja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "What is Nolja Nolja?",
                    "Nolja Nolja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
                QA(
                    "What is Nolja Nolja?",
                    "Nolja Nolja is an entertainment app. We provide video, livestream service. you can earn by watching and buy products in the shop by the points received after watching. "
                ),
            )
        )
    }
    Scaffold(topBar = {
        CommonTopAppBar(
            centeredTitle = true,
            title = stringResource(id = R.string.video_title),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
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
    Column(modifier = Modifier.clickable {
        onClick.invoke()
    }) {
        Row(modifier = Modifier.padding(top = 35.dp, bottom = 12.dp)) {
            Text(qa.question, style = MaterialTheme.typography.bodyLarge.withBold())
        }
        Divider()
    }

}

@Composable
private fun SelectedQAItem(qa: QA, onClick: () -> Unit) {
    Column(modifier = Modifier
        .clickable {
            onClick.invoke()
        }
        .background(Color(0xFFFFFAD0))
        .padding(vertical = 30.dp)) {
        Row {
            Text(qa.question, style = MaterialTheme.typography.bodyLarge.withBold())
        }
        SizeBox(height = 16.dp)
        Text(text = qa.answer, style = MaterialTheme.typography.bodyLarge)
    }
}


private data class QA(
    val question: String,
    val answer: String,
)