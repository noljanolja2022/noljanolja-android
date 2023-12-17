package com.noljanolja.android.common.base

/**
 * Created by tuyen.dang on 12/17/2023.
 */

data class BaseFunCallAPI<T>(
    val key: String,
    val funCallAPI: suspend () ->  Result<T>
)
