package com.noljanolja.android.util

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

fun loginWithKakao(
    context: Context,
    onAuthComplete: (OAuthToken) -> Unit,
    onAuthError: (Throwable) -> Unit
) {
    val TAG = "KAKAO_LOGIN"
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onAuthError(error)
        } else if (token != null) {
            onAuthComplete(token)
        }
    }

// If Kakao Talk is installed on user's device, proceed to log in with Kakao Talk. Otherwise, implement to log in with Kakao Account.
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "Login failed.", error)

                // After installing Kakao Talk, if a user does not complete app permission and cancels Login with Kakao Talk, skip to log in with Kakao Account, considering that the user does not want to log in.
                // You could implement other actions such as going back to the previous page.
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // If a user is not logged into Kakao Talk after installing Kakao Talk and allowing app permission, make the user log in with Kakao Account.
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                Log.i(TAG, "Login succeeded. ${token.accessToken}")
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}