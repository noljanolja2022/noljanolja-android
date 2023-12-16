package com.noljanolja.core.shop.data.model.request

/**
 * Created by tuyen.dang on 11/20/2023.
 */

data class GetItemChooseRequest(
    val page: Int,
    val pageSize: Int,
    val query: String? = null,
    val locale: String
)
