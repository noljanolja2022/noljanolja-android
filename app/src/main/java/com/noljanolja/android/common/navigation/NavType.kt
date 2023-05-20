package com.noljanolja.android.common.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.noljanolja.core.utils.defaultJson
import kotlinx.serialization.decodeFromString
import java.io.Serializable

inline fun <reified T : Serializable> serializableType() = object : NavType<T>(false) {
    override fun get(bundle: Bundle, key: String): T? {
        return bundle.getSerializable(key) as? T
    }

    override fun parseValue(value: String): T {
        return defaultJson().decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putSerializable(key, value)
    }
}

@kotlinx.serialization.Serializable
data class NavObject<T>(
    val data: T,
) : Serializable
