package com.noljanolja.core.event.domain.model

data class Even(
    val action: String,
    val content: String,
    val description: String,
    val endTime: String,
    val id: Int,
    val image: String,
    val isActive: Boolean,
    val priority: String,
    val startTime: String,
    val title: String
)