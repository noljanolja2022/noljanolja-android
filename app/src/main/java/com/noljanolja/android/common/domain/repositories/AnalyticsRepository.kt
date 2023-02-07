package com.noljanolja.android.common.domain.repositories

interface AnalyticsRepository {
    fun sendEvent(
        eventName: String,
        params: MutableMap<String, Any>? = null,
        destinationSdk: DestinationSdkType = DestinationSdkType.FIREBASE
    )

    companion object {
        const val SOURCE_ACTION = "source_value"
        const val SOURCE_SCREEN = "source_screen"
        const val SESSION_NUMBER = "session_number"
        const val DEVICE_ID = "device_id"
        const val USER_ID = "user_id"
        const val APP_VERSION_NAME = "app_version_name"
        const val APP_VERSION_CODE = "app_version_code"
    }
}

enum class DestinationSdkType {
    FIREBASE,
}