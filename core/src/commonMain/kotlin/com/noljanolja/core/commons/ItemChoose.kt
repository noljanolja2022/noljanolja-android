package com.noljanolja.core.commons

import kotlinx.serialization.SerialName

/**
 * Created by tuyen.dang on 11/19/2023.
 */

data class ItemChoose(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    var isSelected: Boolean = false
)
