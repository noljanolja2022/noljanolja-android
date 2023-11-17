package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.Orange300
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.formatDigitsNumber
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.core.shop.domain.model.Gift

@Composable
fun GiftItem(
    gift: Gift,
    modifier: Modifier = Modifier,
    onClick: (Gift) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    Surface(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(15.dp),
        shadowElevation = 10.dp,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
                .padding(6.dp)
                .clickable { onClick.invoke(gift) }
        ) {
            AsyncImage(
                model = gift.image,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.FillBounds,
                placeholder = painterResource(id = R.drawable.ic_gift),
            )
            SizeBox(width = 20.dp)
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = gift.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.secondaryTextColor(),
                )
                Text(
                    gift.brand.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        gift.price.formatDigitsNumber(),
                        style = MaterialTheme.typography.bodyMedium.withBold(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    SizeBox(width = 10.dp)
                    Text(
                        text = stringResource(id = R.string.common_cash),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Orange300)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}