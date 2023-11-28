package com.noljanolja.core.shop.data.model.response

import com.noljanolja.core.base.*
import com.noljanolja.core.commons.*
import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 11/20/2023.
 */

@Serializable
data class GetItemChooseResponse(
    override val code: Int,
    override val message: String,
    override val data: List<ItemChoose>? = null,
) : BaseResponse<List<ItemChoose>>()
