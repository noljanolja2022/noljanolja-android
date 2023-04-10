package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersResponse(
    override val code: Int,
    override val message: String,
    override val data: List<UserRemoteModel> = listOf(),
) : BaseResponse<List<UserRemoteModel>>()