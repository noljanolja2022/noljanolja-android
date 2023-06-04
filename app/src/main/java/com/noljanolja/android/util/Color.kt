package com.noljanolja.android.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.noljanolja.android.ui.theme.NeutralGrey

@Composable
fun MaterialTheme.primaryTextColor() = colorScheme.onBackground

@Composable
fun MaterialTheme.secondaryTextColor(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralGrey
} else {
    colorScheme.onSurfaceVariant
}

@Composable
fun MaterialTheme.lightTextColor() = colorScheme.background
