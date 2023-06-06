package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.secondaryTextColor

@Composable
fun ProductItem(modifier: Modifier = Modifier, index: Int) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 8.5.dp)
    ) {
        AsyncImage(
            model = "https://media.vov.vn/sites/default/files/styles/large/public/2022-03/cf.png",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = "Statbuck $index",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            "Ice latte",
            style = MaterialTheme.typography.titleSmall
        )
        SizeBox(height = 10.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("4800", style = MaterialTheme.typography.labelMedium)
            SizeBox(width = 10.dp)
            Text(
                text = "3800",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W800,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Text(
                text = "Points",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}