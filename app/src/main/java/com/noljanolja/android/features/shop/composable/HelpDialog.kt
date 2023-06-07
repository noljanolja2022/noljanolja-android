package com.noljanolja.android.features.shop.composable

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.noljanolja.android.R

@Composable
fun HelpDialog(
    visible: Boolean = false,
    topPosition: Float,
    onDismissRequest: () -> Unit,
) {
    val density = LocalDensity.current
    val topInDp = with(density) { topPosition.toDp() + 18.dp }
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),

        ) {
            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
            dialogWindowProvider.window.setGravity(Gravity.TOP)
            Column(
                modifier = Modifier
                    .clickable {
                        onDismissRequest.invoke()
                    }
                    .padding(start = 100.dp, end = 16.dp, top = topInDp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(enabled = false) {}
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.shop_help_description),
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.shop_got_it).uppercase())
                }
            }
        }
    }
}