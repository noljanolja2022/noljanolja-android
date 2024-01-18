package com.noljanolja.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.noljanolja.android.common.enums.*

private fun lightColorScheme(appColorSetting: EAppColorSetting) = lightColorScheme(
    primary = appColorSetting.primary,
    onPrimary = NeutralDarkGrey,
    primaryContainer = appColorSetting.primaryContainer,
    onPrimaryContainer = NeutralDarkGrey,

    secondary = appColorSetting.secondary,
    secondaryContainer = appColorSetting.secondaryContainer,
    onSecondary = Color.White,
    onSecondaryContainer = NeutralDarkGrey,

    tertiary = appColorSetting.tertiary,
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

private fun darkColorScheme(appColorSetting: EAppColorSetting) = darkColorScheme(
    primary = appColorSetting.primary,
    onPrimary = Color(0xFF263500),
    primaryContainer = appColorSetting.primaryContainer,
    onPrimaryContainer = NeutralDarkGrey,

    secondary = appColorSetting.secondary,
    secondaryContainer = appColorSetting.secondaryContainer,
    onSecondary = Color.White,
    onSecondaryContainer = NeutralDarkGrey,

    tertiary = appColorSetting.tertiary,
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
    appColorSetting: EAppColorSetting,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            darkColorScheme(appColorSetting)
        } else {
            lightColorScheme(appColorSetting)
        },
        typography = Typography,
        content = content,
    )
}

@Composable
fun MaterialTheme.colorBackgroundWalletAbove(key: Int) =
    if (key == EAppColorSetting.KEY_WARM_GOLD_COLOR) colorScheme.primaryContainer else colorScheme.primary

@Composable
fun MaterialTheme.colorBackgroundWallet(key: Int) =
    if (key == EAppColorSetting.KEY_WARM_GOLD_COLOR) Gold400 else shopBackground()

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
    Yellow00

@Composable
fun MaterialTheme.systemGreen(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        Color(0xFF34C759)
    } else {
        Color(0xFF34C759)
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
fun MaterialTheme.colorMyChatText() = NeutralDarkGrey

@Composable
fun MaterialTheme.darkContent() = NeutralDarkGrey

@Composable
fun MaterialTheme.disableBackgroundColor(darkTheme: Boolean = isSystemInDarkTheme()) =
    if (darkTheme) {
        NeutralDeepGrey
    } else {
        NeutralLightGrey
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

@Composable
fun backgroundChoseItemColor(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) {
    NeutralDarkGrey
} else {
    Platinum
}
