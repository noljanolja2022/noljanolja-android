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

    var showNewChatDialog: Boolean
        get() = sharePreference.getBoolean(SHOW_NEW_CHAT_DIALOG, true)
        set(value) {
            sharePreference.run {
                edit().putBoolean(SHOW_NEW_CHAT_DIALOG, value).apply()
            }
        }

    var needCheckRequestGoogle: Boolean
        get() = sharePreference.getBoolean(REQUEST_LOGIN_GOOGLE, true)
        set(value) {
            sharePreference.run {
                edit().putBoolean(REQUEST_LOGIN_GOOGLE, value).apply()
            }
        }

    companion object {
        const val YOUTUBE_TOKEN = "youtube_token"
        const val SHOW_NEW_CHAT_DIALOG = "show_new_chat_dialog"
        const val REQUEST_LOGIN_GOOGLE = "request_login_google"
    }
}