package com.noljanolja.android.features.home.wallet.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.theme.LightBlue
import com.noljanolja.android.ui.theme.systemBlue

@Composable
fun TimeHeader(time: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(32.dp)
            .background(LightBlue)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            time,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Expanded()
        Text(
            stringResource(id = R.string.wallet_history_dashboard),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.systemBlue(),
            modifier = Modifier.clickable {
                onClick.invoke()
            }.padding(end = 5.dp)
        )
        IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.systemBlue()
            )
        }
    }
}