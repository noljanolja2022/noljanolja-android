package com.noljanolja.core.commons

import kotlinx.serialization.Serializable

/**
 * Created by tuyen.dang on 11/19/2023.
 */

@Serializable
data class ItemChoose(
    val id: String = "",
    val image: String? = "",
    val name: String = "",
    var isSelected: Boolean = false
)
