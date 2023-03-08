package com.noljanolja.android.common.user.domain.model.response

import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.core.base.BaseResponse
import kotlinx.serialization.Serializable

@Serializable
data class SyncUserContactsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<User> = listOf(),
) : BaseResponse<List<User>>()