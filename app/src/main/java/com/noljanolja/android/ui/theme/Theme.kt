package com.noljanolja.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    surfaceVariant = NeutralLightGrey,
    onSurfaceVariant = NeutralDeepGrey,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color(0xFF263500),
    primaryContainer = Color(0xFF384E00),
    onPrimaryContainer = Color(0xFFC0F44A),

    secondary = YellowMain,
    secondaryContainer = Color(0xFF373100),
    onSecondary = Color(0xFF504700),
    onSecondaryContainer = Color(0xFFFDE40F),

    tertiary = BlueMain,
    tertiaryContainer = Blue00,
    onTertiary = Color(0xFF263500),
    onTertiaryContainer = Color(0xFFC0F44A),

    error = Color(0xFFFF4F4F),
    errorContainer = Color(0xFFFF4F4F),

    background = NeutralDarkGrey,
    outline = NeutralGrey,
    onBackground = Color(0xFFE4E3DB),
    surface = Color(0xFF45483C),
    surfaceVariant = Color(0xFF45483C),
    onSurfaceVariant = Color(0xFFC6C8B9),

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
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content,
    )
}

@Composable
fun MaterialTheme.colorBackground(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralDarkGrey
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