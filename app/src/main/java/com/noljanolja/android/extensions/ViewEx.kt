package com.noljanolja.android.extensions

import androidx.constraintlayout.compose.*

/**
 * Created by tuyen.dang on 11/8/2023.
 */

internal fun setVisibility(statement: Boolean): Visibility =
    if (statement) Visibility.Visible else Visibility.Gone
