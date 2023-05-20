package com.noljanolja.android.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MaterialTheme.primaryTextColor() = colorScheme.onBackground

@Composable
fun MaterialTheme.secondaryTextColor() = colorScheme.onSurfaceVariant
