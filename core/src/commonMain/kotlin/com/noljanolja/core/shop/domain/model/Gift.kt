package com.noljanolja.core.shop.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Gift(
    val brand: Brand = Brand(),
    val category: Category = Category(),
    val code: String = "",
    val codes: List<String> = emptyList(),
    val description: String = "",
    val endTime: String = "",
    val id: Long = 0,
    val image: String = "",
    val isPurchasable: Boolean = false,
    val name: String = "",
    val price: Long = 0,
    val remaining: Long = 0,
    val startTime: String = "",
    val total: Long = 0,
)

@Serializable
data class Category(
    val code: String = "",
    val id: Long = 0,
    val image: String = "",
)

@Serializable
data class Brand(
    val id: Long = 0,
    val image: String = "",
    val name: String = "",
)