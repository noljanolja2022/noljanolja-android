package com.noljanolja.android.util

import com.kakao.sdk.auth.model.OAuthToken
import com.noljanolja.android.data.model.User

fun OAuthToken.toDomainUser() = User(
    token = accessToken
)