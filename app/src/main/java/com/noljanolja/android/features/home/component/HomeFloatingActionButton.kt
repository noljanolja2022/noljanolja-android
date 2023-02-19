package com.noljanolja.android.features.home.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R

@Composable
fun HomeFloatingActionButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        shape = CircleShape,
        backgroundColor = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .size(68.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_wallet),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
    }
}
