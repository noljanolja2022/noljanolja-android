package com.noljanolja.core.event.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetBannersRequest(val page: Int = 0, val pageSize: Int = 100)