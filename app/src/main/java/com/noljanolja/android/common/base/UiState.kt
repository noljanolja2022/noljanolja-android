package com.noljanolja.android.common.base

abstract class UiState<D>(
    open val loading: Boolean,
    open val error: Throwable?,
    open val data: D?,
)
