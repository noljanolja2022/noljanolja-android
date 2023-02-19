package com.noljanolja.android.common.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ListTile(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    @DrawableRes leadingDrawable: Int? = null,
    @DrawableRes trailingDrawable: Int? = null,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(modifier),
    ) {
        leadingDrawable?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "start icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Black,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(horizontalAlignment = Alignment.Start) {
            title()
            description?.let {
                Spacer(modifier = Modifier.padding(8.dp))
                it()
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        trailingDrawable?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "start icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
fun RoundedListTile(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    @DrawableRes leadingDrawable: Int? = null,
    @DrawableRes trailingDrawable: Int? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = MaterialTheme.colorScheme.secondary,
            )
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onPrimary),

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { onClick() }
                .padding(vertical = 8.dp, horizontal = 12.dp),
        ) {
            leadingDrawable?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "start icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                title()
                description?.let { it() }
            }
            Spacer(modifier = Modifier.weight(1f))
            trailingDrawable?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "start icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
