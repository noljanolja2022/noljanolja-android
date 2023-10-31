package com.noljanolja.android.common.ads.interstitial

import android.app.Activity

interface Interstitial {
    fun isReady(): Boolean
    suspend fun load(activity: Activity): LoadResult
    fun show(
        activity: Activity,
        enableDialog: Boolean,
        onCompleted: (result: Any?) -> Unit,
    )
}

sealed class LoadResult {
    object Success : LoadResult()
    data class Error(val code: Int? = null, val message: String? = null) : LoadResult()
}
