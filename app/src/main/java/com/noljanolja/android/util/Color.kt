package com.noljanolja.android.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.NeutralLightGrey

@Composable
fun MaterialTheme.primaryTextColor() = colorScheme.onBackground

@Composable
fun MaterialTheme.secondaryTextColor(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralLightGrey
} else {
    colorScheme.onSurfaceVariant
}

@Composable
fun MaterialTheme.lightTextColor() = colorScheme.background
