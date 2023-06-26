package com.noljanolja.android.features.shop.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    memberPoint: Long,
    modifier: Modifier = Modifier,
    onClick: (Gift) -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick.invoke(gift) }.clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 8.5.dp)
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
            style = MaterialTheme.typography.titleSmall
        )
        FlowRow(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.W800,
                            fontSize = 16.sp,
                            color = Orange300
                        )
                    ) {
                        append(gift.price.formatDigitsNumber())
                    }
                    append(" ")
                    withStyle(
                        SpanStyle(fontSize = 16.sp)
                    ) {
                        append(stringResource(id = R.string.common_points))
                    }
                },
            )
        }
    }
}