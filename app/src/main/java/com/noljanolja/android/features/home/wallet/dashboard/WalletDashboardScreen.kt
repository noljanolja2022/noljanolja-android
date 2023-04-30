package com.noljanolja.android.features.home.wallet.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.ui.composable.SizeBox

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletDashboardScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 16.dp)
    ) {
        WalletDashboardContent()
    }
}

@Composable
private fun WalletDashboardContent() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        RecentTransactions()
    }
}

@Composable
private fun TransactionsChart() {
}

@Composable
private fun RecentTransactions() {
    val paddingHorizontalModifier = Modifier.padding(horizontal = 12.dp)
    LazyColumn(
        modifier = Modifier
            .clip(
                RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.background)

    ) {
        item {
            Text(
                "Recent transactions",
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
            )
            SizeBox(height = 9.dp)
            Text(
                text = "Wednesday",
                modifier = paddingHorizontalModifier,
                style = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                "March 1st, 2023",
                style = MaterialTheme.typography.labelMedium,
                modifier = paddingHorizontalModifier
            )
        }

        items(count = 5) { index ->
            if (index != 0) {
                Divider(thickness = 1.dp)
            }
            SizeBox(height = 8.dp)
            Row(
                modifier = paddingHorizontalModifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Buy Emoticon set",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    "- 15000",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            SizeBox(height = 8.dp)
        }
    }
}