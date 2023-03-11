package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.user.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class SyncUserContactsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<UserRemoteModel> = listOf(),
) : BaseResponse<List<UserRemoteModel>>()