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
    onPrimaryContainer = NeutralDarkGrey,

    secondary = YellowMain,
    secondaryContainer = Yellow00,
    onSecondary = Color.White,
    onSecondaryContainer = NeutralDarkGrey,

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
    primaryContainer = LightGreen,
    onPrimaryContainer = NeutralDarkGrey,

    secondary = YellowMain,
    secondaryContainer = Yellow00,
    onSecondary = Color.White,
    onSecondaryContainer = NeutralDarkGrey,

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
fun MaterialTheme.shopItemBackground(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    DeeperGrey.copy(alpha = 0.3f)
} else {
    NeutralLight
}

@Composable
fun MaterialTheme.shopBackground(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    Color(0xFF292929)
} else {
    NeutralLight
}

@Composable
fun MaterialTheme.colorBackgroundTransaction(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Green300
    } else {
        colorScheme.secondaryContainer
    }

@Composable
fun MaterialTheme.primaryColor() = colorScheme.primary

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

@Composable
fun MaterialTheme.colorMyChatText(darkTheme: Boolean = isSystemInDarkTheme()) =
    NeutralDarkGrey

@Composable
fun MaterialTheme.colorMyChatTime(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFF007AFF)
    } else {
        Color(0xFF007AFF)
    }

@Composable
fun MaterialTheme.green300() = Color(0xFF4F6D00)

@Composable
fun MaterialTheme.darkContent() = NeutralDarkGrey

@Composable
fun MaterialTheme.lightContent() = Color.LightGray

@Composable
fun MaterialTheme.disableBackgroundColor(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        NeutralDeepGrey
    } else {
        NeutralLightGrey
    }

@Composable
fun MaterialTheme.helpIconColor(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    colorScheme.primary
} else {
    NeutralDarkGrey
}

@Composable
fun MaterialTheme.green300(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    LightGreen
} else {
    Green300
}

@Composable
fun MaterialTheme.backgroundInPopup(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralDeepGrey
} else {
    colorScheme.background
}

@Composable
fun textColor(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    Color.White
} else {
    Color.Black
}
