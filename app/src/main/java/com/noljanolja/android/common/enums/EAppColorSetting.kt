package com.noljanolja.android.common.enums

import androidx.compose.ui.graphics.*
import com.noljanolja.android.ui.theme.*

/**
 * Created by tuyen.dang on 12/27/2023.
 */

enum class EAppColorSetting(
    val primary: Color,//Green 200 Main
    val primaryContainer: Color,//Green 50
    val onPrimaryContainer: Color,// Green 400
    val secondary: Color,//Green 300
    val secondaryContainer: Color,//Green 100,
    val tertiary: Color//title Video
) {
    DefaultPrimaryColors(
        primary = Green200Main,
        primaryContainer = Green50,
        onPrimaryContainer = Green400,
        secondary = Green300,
        secondaryContainer = Green100,
        tertiary = Orange00
    ),
    ElegantBluePrimaryColors(
        primary = Blue200Main,
        primaryContainer = Blue50,
        onPrimaryContainer = Blue400,
        secondary = Blue300,
        secondaryContainer = Blue100,
        tertiary = Blue50
    ),
    WarmGoldPrimaryColors(
        primary = Gold200Main,
        primaryContainer = Gold50,
        onPrimaryContainer = Gold400,
        secondary = Gold300,
        secondaryContainer = Gold100,
        tertiary = Gold50
    );
    companion object {
        internal const val KEY_DEFAULT_COLOR = 1
        internal const val KEY_ELEGANT_BLUE_COLOR = 2
        internal const val KEY_WARM_GOLD_COLOR = 3

        internal fun getColorByKey(key: Int): EAppColorSetting =
            when(key) {
                KEY_ELEGANT_BLUE_COLOR -> ElegantBluePrimaryColors
                KEY_WARM_GOLD_COLOR -> WarmGoldPrimaryColors
                else -> DefaultPrimaryColors
            }
    }
}


