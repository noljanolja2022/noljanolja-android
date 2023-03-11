package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse

@kotlinx.serialization.Serializable
data class UpdateUserResponse(
    override val code: Int,
    override val message: String,
    override val data: Preferences,
) : BaseResponse<UpdateUserResponse.Preferences>() {
    @kotlinx.serialization.Serializable
    data class Preferences(
        val collectAndUsePersonalInfo: Boolean? = null,
    )
}