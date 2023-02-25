package com.noljanolja.android.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    modifier: Modifier,
    searchText: String,
    hint: String,
    onSearch: (String) -> Unit = {},
    onSearchButton: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Box(
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchText,
                onValueChange = onSearch,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchButton() }),
                cursorBrush = SolidColor(LocalContentColor.current),
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
                modifier = Modifier.padding(start = 12.dp)
                    .clip(CircleShape)
                    .clickable { onSearch("") },
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        modifier = Modifier,
        searchText = "Search",
        hint = "hint"
    ) {}
}