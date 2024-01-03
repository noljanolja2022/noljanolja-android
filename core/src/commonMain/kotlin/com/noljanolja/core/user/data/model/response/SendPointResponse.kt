package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.*
import com.noljanolja.core.contacts.domain.model.*
import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/3/2024.
 */

@Serializable
data class SendPointResponse(
    override val code: Int,
    override val message: String,
    override val data: UserSendPoint = UserSendPoint()
) : BaseResponse<UserSendPoint>()

