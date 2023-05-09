package com.noljanolja.core.loyalty.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.loyalty.domain.model.MemberInfo

@kotlinx.serialization.Serializable
internal data class GetMemberInfoResponse(
    override val code: Int,
    override val message: String,
    override val data: MemberInfo?,
) : BaseResponse<MemberInfo>()