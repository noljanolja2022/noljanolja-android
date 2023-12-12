package com.noljanolja.android.extensions

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.noljanolja.android.util.Constant.DefaultValue.MAX_SCALE_OF_SIZE

/**
 * Created by tuyen.dang on 12/12/2023.
 */


@Composable
internal fun getScaleSize(): Float {
    val configuration = LocalConfiguration.current
    return if (configuration.fontScale < MAX_SCALE_OF_SIZE) configuration.fontScale else MAX_SCALE_OF_SIZE
}
 