package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.secondaryTextColor

@Composable
fun CouponItem(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
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
        SizeBox(height = 3.dp)
        Text(
            text = "Statbuck",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.secondaryTextColor()
        )
        Text(
            "Ice latte",
            style = MaterialTheme.typography.labelLarge
        )
        SizeBox(height = 5.dp)
        PrimaryButton(
            text = "Use now".uppercase(),
            containerColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        ) {
        }
    }
}