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

@kotlinx.serialization.Serializable
data class UpdateAvatarResponse(
    override val code: Int,
    override val message: String,
    override val data: UpdateAvatarData,
) : BaseResponse<UpdateAvatarResponse.UpdateAvatarData>() {
    @kotlinx.serialization.Serializable
    data class UpdateAvatarData(
        val path: String = "",
        val size: Long = 0,
        val md5: String = "",
    )
}