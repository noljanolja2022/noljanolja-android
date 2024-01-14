package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.*
import com.noljanolja.core.contacts.domain.model.*
import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 1/14/2024.
 */

@Serializable
data class GetNotificationsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<NotificationData> = emptyList()
) : BaseResponse<List<NotificationData>>()
