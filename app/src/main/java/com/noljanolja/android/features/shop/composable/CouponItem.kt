package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange00
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.shop.domain.model.Gift

@Composable
fun CouponItem(
    gift: Gift,
    modifier: Modifier = Modifier,
    onUse: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = modifier.wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            AsyncImage(
                model = gift.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            SizeBox(height = 3.dp)
            Text(
                text = gift.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.secondaryTextColor()
            )
            Text(
                gift.brand.name,
                style = MaterialTheme.typography.labelLarge
            )
            SizeBox(height = 5.dp)
            PrimaryButton(
                text = stringResource(id = R.string.common_use_now).uppercase(),
                containerColor = Orange00,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(5.dp),
                onClick = onUse
            )
        }
    }
}