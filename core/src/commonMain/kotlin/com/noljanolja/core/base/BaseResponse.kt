package com.noljanolja.core.base

abstract class BaseResponse<D> {
    abstract val code: Int
    abstract val message: String
    open val data: D? = null

    fun isSuccessful(): Boolean = code == 0 || (code in 200..210)
}