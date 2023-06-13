package com.noljanolja.android.common.base

data class UiState<D>(
    val loading: Boolean = false,
    val error: Throwable? = null,
    val data: D? = null,
) {
    fun getOrDefault(default: D) = data ?: default
}