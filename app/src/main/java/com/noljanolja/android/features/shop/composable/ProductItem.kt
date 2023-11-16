package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CustomText
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.shop.domain.model.Gift

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductItem(
    gift: Gift,
    modifier: Modifier = Modifier,
    onClick: (Gift) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        shadowElevation = 5.dp,
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(15.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick.invoke(gift) }
                .clip(RoundedCornerShape(25.dp))
                .padding(8.dp),
        ) {
            AsyncImage(
                model = gift.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().aspectRatio(1F).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            SizeBox(height = 5.dp)
            CustomText(
                text = gift.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.secondaryTextColor(),
                lines = 2
            )
            Text(
                gift.brand.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    gift.price.formatDigitsNumber(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W800,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(id = R.string.common_cash),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Orange300)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
            SizeBox(height = 12.dp)
        }
    }
}