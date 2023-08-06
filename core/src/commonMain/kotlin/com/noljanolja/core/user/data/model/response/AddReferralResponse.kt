package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse

@kotlinx.serialization.Serializable
data class AddReferralResponse(
    override val code: Int,
    override val message: String,
    override val data: AddReferralData = AddReferralData(),
) : BaseResponse<AddReferralResponse.AddReferralData>() {
    @kotlinx.serialization.Serializable

    data class AddReferralData(
        val rewardPoints: Long = 0L,
    )
}