package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import coil.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.ROUND_RECTANGLE
import com.noljanolja.core.commons.*
import com.noljanolja.core.shop.domain.model.*

/**
 * Created by tuyen.dang on 11/19/2023.
 */

@Composable
internal fun ItemType(
    modifier: Modifier = Modifier,
    item: ItemChoose,
    borderColor: Color = Color.Transparent,
    borderColorSelected: Color = Color.Transparent,
    textColor: Color = textColor(),
    textColorSelected: Color = Color.Black,
    bgColor: Color = backgroundChoseItemColor(),
    bgColorSelected: Color = PrimaryGreen,
    onItemClick: (ItemChoose) -> Unit,
) {
    item.run {
        Text(
            text = name,
            style = Typography.bodySmall.copy(
                color = if (isSelected) textColorSelected else textColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .wrapContentHeight()
                .border(
                    BorderStroke(
                        width = (0.5f).dp,
                        color = if (isSelected) borderColorSelected else borderColor
                    ), shape = RoundedCornerShape(5.dp)
                )
                .clickable {
                    onItemClick(this@run)
                }
                .background(
                    color = if (isSelected) bgColorSelected else bgColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .then(modifier))
    }
}

@Composable
internal fun ProductSection(
    modifier: Modifier = Modifier,
    gift: Gift,
    containerColor: Color = MaterialTheme.colorScheme.background,
    onItemClick: (Gift) -> Unit
) {
    val roundedCornerShape = RoundedCornerShape(ROUND_RECTANGLE.dp)
    gift.run {
        Surface(
            modifier = modifier
                .fillMaxWidth(),
            shape = roundedCornerShape,
            shadowElevation = 10.dp,
            color = containerColor
        ) {
            ConstraintLayout(
                modifier = modifier
                    .wrapContentHeight()
                    .width(140.dp)
                    .clip(roundedCornerShape)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = roundedCornerShape
                    )
                    .clickable {
                        onItemClick(gift)
                    }
            ) {
                val (img, tvBrand, tvName, tvVoucher, tvPrice, tvPriceAll, marginBottom) = createRefs()
                AsyncImage(
                    model = gift.image,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                        .background(MaterialTheme.colorScheme.surface)
                        .constrainAs(img) {
                            top.linkTo(parent.top)
                            linkTo(start = parent.start, end = parent.end)
                        },
                )
                Text(
                    text = gift.brand.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.secondaryTextColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .constrainAs(tvBrand) {
                            top.linkTo(img.bottom, 8.dp)
                            linkTo(start = parent.start, end = parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
                Text(
                    text = gift.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .constrainAs(tvName) {
                            top.linkTo(tvBrand.bottom)
                            linkTo(start = parent.start, end = parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
                Text(
                    text = gift.price.formatDigitsNumber(),
                    style = MaterialTheme.typography.bodyMedium.withBold(),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 5.dp)
                        .constrainAs(tvPrice) {
                            top.linkTo(tvName.bottom, 5.dp)
                            linkTo(
                                start = parent.start,
                                end = tvVoucher.start,
                                bias = 0.0f
                            )
                        }
                )
                Text(
                    text = stringResource(id = R.string.common_cash),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Orange300)
                        .constrainAs(tvVoucher) {
                            linkTo(top = tvPrice.top, bottom = tvPrice.bottom)
                            linkTo(
                                start = tvPrice.end,
                                end = parent.end,
                                bias = 0.0f
                            )
                        }
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                )
                createHorizontalChain(tvPrice, tvVoucher, chainStyle = ChainStyle.Packed)
                Text(
                    text = "4800",
                    style = MaterialTheme.typography.labelSmall.copy(
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = MaterialTheme.secondaryTextColor(),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .constrainAs(tvPriceAll) {
                            top.linkTo(tvVoucher.bottom)
                            linkTo(start = parent.start, end = parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
                Spacer(
                    modifier = Modifier
                        .height(17.dp)
                        .constrainAs(marginBottom) {
                            top.linkTo(tvPriceAll.bottom)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProductSection() {
    ProductSection(
        gift = Gift(
            image = "https://media.vneconomy.vn/w800/images/upload/2021/04/20/DC_Dat-33309.jpg",
            brand = ItemChoose(
                name = "Starbucks"
            ),
            name = "Caramel Mocha",
            price = 3.8001231231231232E17
        ),
        onItemClick = {

        }
    )
}
