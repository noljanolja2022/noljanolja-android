package com.noljanolja.core.user.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PushTokensRequest(
    val userId: String,
    val deviceType: DeviceType = DeviceType.MOBILE,
    val deviceToken: String,
)

enum class DeviceType {
    MOBILE, DESKTOP
}