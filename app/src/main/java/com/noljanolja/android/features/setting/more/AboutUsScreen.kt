package com.noljanolja.android.features.setting.more

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.darkText
import com.noljanolja.android.util.secondaryTextColor
import org.koin.androidx.compose.getViewModel

@Composable
fun AboutUsScreen(
    viewModel: AppInfoViewModel = getViewModel()
) {
    val items by remember {
        mutableStateOf(
            listOf(
                AboutUsItem("Company Name", "PNP&YY Co., Ltd."),
                AboutUsItem("Representative", "Seungdae Park"),
                AboutUsItem(
                    "Address", "Room 809, Ace Gasan Tower, 121 Digital-ro, Geumcheon-gu, Seoul"
                ),
                AboutUsItem("Phone call", "070-7733-1193")
            )
        )
    }
    AboutUsContent(
        aboutUsItems = items, handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutUsContent(
    aboutUsItems: List<AboutUsItem>,
    handleEvent: (AppInfoEvent) -> Unit,
) {
    Scaffold(topBar = {
        CommonTopAppBar(centeredTitle = true,
            title = stringResource(id = R.string.setting_about_us_title),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onBack = {
                handleEvent(AppInfoEvent.Back)
            })
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(aboutUsItems) { item ->
                SizeBox(height = 16.dp)
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.secondaryTextColor()
                )
                SizeBox(height = 5.dp)
                Text(text = item.value, style = MaterialTheme.typography.bodyMedium)
                SizeBox(height = 16.dp)
                Divider()
            }
        }
    }
}

private data class AboutUsItem(
    val title: String,
    val value: String,
)