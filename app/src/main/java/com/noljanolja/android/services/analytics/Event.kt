package com.noljanolja.android.services.analytics

import android.os.Bundle
import com.noljanolja.android.BuildConfig

sealed class Event(
    val name: String,
    val data: MutableMap<String, String> = mutableMapOf()
) {
    abstract class Navigate(
        navigated: String,
        data: MutableMap<String, String> = mutableMapOf()
    ) : Event(
        name = navigated,
        data = data
    )

    abstract class Click(
        action: String,
        data: MutableMap<String, String> = mutableMapOf()
    ) : Event(
        name = action,
        data = data
    )

    abstract class Switch(
        changed: String,
        data: MutableMap<String, String> = mutableMapOf()
    ) : Event(
        name = changed,
        data = data
    )
}

internal fun Event.getDataAsBundle(): Bundle? = data?.let {
    Bundle().apply {
        data.forEach { (key, value) -> putString(key, value) }
    }
}

internal fun Event.setGlobalParams(extraData: Map<String, String>? = null) {
    data.let {
        data[EventKey.APP_VERSION_NAME] = BuildConfig.VERSION_NAME
        data[EventKey.APP_VERSION_CODE] = BuildConfig.VERSION_CODE.toString()
        extraData?.forEach { (key, value) ->
            data[key] = value
        }
    }
}

object EventKey {
    const val SOURCE_ACTION = "source_value"
    const val SOURCE_SCREEN = "source_screen"
    const val SESSION_NUMBER = "session_number"
    const val DEVICE_ID = "device_id"
    const val USER_ID = "user_id"
    const val APP_VERSION_NAME = "app_version_name"
    const val APP_VERSION_CODE = "app_version_code"
}
