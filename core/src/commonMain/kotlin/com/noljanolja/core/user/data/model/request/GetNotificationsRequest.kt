package com.noljanolja.core.user.data.model.request

/**
 * Created by tuyen.dang on 1/14/2024.
 */

data class GetNotificationsRequest(
    val page: Int = 1,
    val pageSize: Int = 100
)
