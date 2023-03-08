package com.noljanolja.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = YellowMain,
    secondary = OrangeMain,
    tertiary = BlueMain,
)

private val LightColorScheme = lightColorScheme(
    primary = YellowMain,
    onPrimary = NeutralDarkGrey,
    primaryContainer = Yellow00,
    onPrimaryContainer = NeutralGrey,

    secondary = OrangeMain,
    secondaryContainer = Orange00,
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
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
