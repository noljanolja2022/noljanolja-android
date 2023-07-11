package com.noljanolja.android.common.sharedpreference

import android.content.Context
import com.noljanolja.android.BuildConfig
import kotlinx.datetime.Clock

class SharedPreferenceHelper(private val context: Context) {
    private val sharePreference =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

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

    var loginOtpTime: Long
        get() = let {
            val now = Clock.System.now().toEpochMilliseconds()
            val loggedTime = now - sharePreference.getLong(KEY_LOGIN_OTP_TIME, 0L)
            (90_000 - loggedTime).takeIf { it > 0L } ?: 90_000
        }
        set(_) {
            val now = Clock.System.now().toEpochMilliseconds()
            sharePreference.run {
                edit().putLong(KEY_LOGIN_OTP_TIME, now).apply()
            }
        }

    var seenBanners: List<Long>
        get() = let {
            val arrayString = sharePreference.getString(KEY_SEEN_BANNERS, "").orEmpty()
            arrayString.split(",").mapNotNull { it.toLongOrNull() }
        }
        set(value) {
            sharePreference.run {
                val arrayString = sharePreference.getString(KEY_SEEN_BANNERS, "").orEmpty()
                val seenBanners = arrayString.split(",").mapNotNull { it.toLongOrNull() }
                edit().putString(KEY_SEEN_BANNERS, (seenBanners + value).joinToString(",")).apply()
            }
        }

    companion object {
        const val YOUTUBE_TOKEN = "youtube_token"
        const val SHOW_NEW_CHAT_DIALOG = "show_new_chat_dialog"
        const val REQUEST_LOGIN_GOOGLE = "request_login_google"
        const val KEY_LOGIN_OTP_TIME = "key_login_otp_time"
        const val KEY_SEEN_BANNERS = "key_seen_banners"
    }
}