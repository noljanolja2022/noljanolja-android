package com.noljanolja.android.util

/**
 * Created by tuyen.dang on 11/5/2023.
 */

class Constant {
    object LocaleDateTime {
        const val FROM_SERVER = "yyyy-MM-dd"
    }
    object LocaleDefine {
        const val KOREAN = "KR"
        const val INDIAN = "IN"
    }

    object Timer {
        const val MILLISECOND_OF_ONE_MINUTE = 60 * 1000L
        const val MILLISECOND_OF_ONE_DAY = 24 * 60 * 60 * 1000L
//        const val TEN_DAY = 10 * 24 * 60 * 60 * 1000L
//        const val ONE_SECOND = 1000L
//        const val ONE_MINUTE = 60
//        const val ONE_DAY = 24 * 60 * 60 * 1000L
//        const val ONE_YEAR = 365 * 24 * 60 * 60 * 1000L
//        const val ONE_MONTH = 30 * 24 * 60 * 60 * 1000L
        const val ONE_HOUR = 60 * 60 * 1000L
    }

    object PackageShareToApp {
        const val FACEBOOK_PACKAGE = "com.facebook.katana"
        const val TWITTER_PACKAGE = "com.twitter.android"
        const val WHATS_APP_PACKAGE = "com.whatsapp"
        const val TELEGRAM_PACKAGE = "org.telegram.messenger"
        const val MESSENGER_PACKAGE = "com.facebook.orca"
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val MESSAGE_APP_PACKAGE = "com.android.mms"
    }
    object AppNameShareToApp {
        const val FACEBOOK = "Facebook"
        const val TWITTER = "Twitter"
        const val WHATS_APP = "WhatsApp"
        const val TELEGRAM = "Telegram"
        const val MESSENGER = "Messenger"
        const val INSTAGRAM = "Instagram"
        const val MESSAGE_APP = "Message"
    }

    object DefaultValue {
        const val PADDING_VIEW = 5
        const val PADDING_ICON = 7
        const val ROUND_RECTANGLE = 10
        const val PADDING_HORIZONTAL_SCREEN = 20
        const val PADDING_VIEW_SCREEN = 16
        const val PADDING_VERTICAL_SCREEN = 24
        const val ROUND_DIALOG = 10
        const val BUTTON_HEIGHT = 48
        const val BUTTON_TITLE = 56
        const val TWEEN_ANIMATION_TIME = 300
        const val MAX_SCALE_OF_SIZE = 1.5f
    }
}
