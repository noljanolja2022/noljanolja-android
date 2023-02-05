package com.noljanolja.android.domain.repositories

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.noljanolja.android.R
import com.noljanolja.android.data.model.User
import com.noljanolja.android.data.repositories.AuthRepository
import com.noljanolja.android.util.toDomainUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl private constructor(
    val context: Context,
    private val googleWebClientId: String,
) : AuthRepository {
    private val _userFlow = MutableStateFlow<User?>(null)
    override fun getCurrentUser() = _userFlow.asStateFlow()

    override suspend fun loginWithKakao(): Result<User> = suspendCoroutine { continuation ->
        val callback = { token: OAuthToken?, error: Throwable? ->
            token?.let {
                continuation.resume(Result.success(token.toDomainUser()))
            } ?: continuation.resume(Result.failure(error!!))
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    override fun getGoogleSignInIntent(): Intent = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .build()
    ).signInIntent

    companion object {
        fun getInstance(
            context: Context,
            kakaoApiKey: String,
            googleWebClientId: String,
        ) = AuthRepositoryImpl(
            context,
            googleWebClientId
        ).also {
            KakaoSdk.init(context, kakaoApiKey)
        }
    }
}
