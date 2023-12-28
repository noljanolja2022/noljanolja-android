package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*

@Composable
fun SearchBar(
    modifier: Modifier,
    searchText: String,
    hint: String,
    enabled: Boolean = true,
    background: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
    onSearch: (String) -> Unit,
    onSearchButton: () -> Unit = {},
    onFocusChange: (FocusState) -> Unit = {},
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(36.dp, 50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged(onFocusChange),
                value = searchText,
                onValueChange = onSearch,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchButton() }),
                cursorBrush = SolidColor(LocalContentColor.current),
                enabled = enabled,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            if (searchText.isEmpty()) {
                Text(
                    hint,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
        if (searchText.isNotEmpty()) {
            Icon(
                Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clip(CircleShape)
                    .clickable { onSearch("") },
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
internal fun SearchBarViewOnly(
    modifier: Modifier,
    hint: String,
    iconTint: Color?,
    textColor: Color,
    background: Color,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(36.dp, 50.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                hint,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = iconTint ?: MaterialTheme.colorScheme.onBackground,
        )
    }
}
