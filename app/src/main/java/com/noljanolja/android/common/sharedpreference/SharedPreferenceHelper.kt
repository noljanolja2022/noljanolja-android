package com.noljanolja.android.common.sharedpreference

import android.content.Context
import com.noljanolja.android.BuildConfig

class SharedPreferenceHelper(private val context: Context) {
    private val sharePreference = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    fun setString(key: String, value: String) {
        sharePreference.run {
            edit().putString(key, value).apply()
        }
    }

    fun getString(key: String, defaultValue: String = ""): String? {
        return sharePreference.getString(key, defaultValue)
    }

    var youtubeToken
        get() = getString(YOUTUBE_TOKEN)
        set(value) {
            setString(YOUTUBE_TOKEN, value.orEmpty())
        }

    companion object {
        const val YOUTUBE_TOKEN = "youtube_token"
    }
}