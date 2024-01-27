package com.noljanolja.android.extensions

import android.util.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.*
import com.noljanolja.android.common.base.*

/**
 * Created by tuyen.dang on 1/28/2024.
 */



fun firebaseRegisterToken(onGetTokenSuccess: suspend (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(
        OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "TTT",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }

            val token = task.result
            launchInMain {
                onGetTokenSuccess(token)
            }
        }
    )
}

fun firebaseRemoveToken() = FirebaseMessaging.getInstance().deleteToken()
 