package com.noljanolja.core.base

@kotlinx.serialization.Serializable
data class ResponseWithoutData(
    override val code: Int,
    override val message: String,
) : BaseResponse<Nothing>()