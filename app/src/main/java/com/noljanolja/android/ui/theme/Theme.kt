package com.noljanolja.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = Orange400,
    tertiary = BlueMain,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = NeutralDarkGrey,
    primaryContainer = LightGreen,
    onPrimaryContainer = NeutralGrey,

    secondary = YellowMain,
    secondaryContainer = Yellow00,
    onSecondary = Color.White,
    onSecondaryContainer = Color.Black,

    tertiary = BlueMain,
    tertiaryContainer = Blue00,
    onTertiary = Color.White,
    onTertiaryContainer = Color.White,

    error = Color(0xFFFF4F4F),
    errorContainer = Color(0xFFFF4F4F),

    background = NeutralLight,
    outline = NeutralGrey,
    onBackground = NeutralDarkGrey,
    surface = NeutralLightGrey,
    surfaceVariant = NeutralLight,
    onSurfaceVariant = NeutralDeepGrey,

)

@Composable
fun NoljanoljaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            LightColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content,
    )
}

@Composable
fun MaterialTheme.colorBackground(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralLight
} else {
    NeutralLight
}

@Composable
fun MaterialTheme.systemGreen(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFF34C759)
    } else {
        Color(0xFF34C759)
    }

@Composable
fun MaterialTheme.systemRed50(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFFFB5141)
    } else {
        Color(0xFFFB5141)
    }

@Composable
fun MaterialTheme.systemRed100(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFFFF3B30)
    } else {
        Color(0xFFFF3B30)
    }

@Composable
fun MaterialTheme.systemBlue(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFF007AFF)
    } else {
        Color(0xFF007AFF)
    }