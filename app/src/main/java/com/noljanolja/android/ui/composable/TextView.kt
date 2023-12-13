package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.ui.theme.*

/**
 * Created by tuyen.dang on 12/13/2023.
 */

@Composable
internal fun TextViewTitle(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyMedium.withBold(),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(end = 5.dp),
            text = title,
            style = titleStyle
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = valueStyle,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
private fun PreviewTextViewTitle() {
    TextViewTitle(
        title = "Text",
        value = "123123"
    )
}
 