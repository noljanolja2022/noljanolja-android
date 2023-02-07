package com.noljanolja.android.data.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.noljanolja.android.domain.model.User
import com.noljanolja.android.domain.repositories.AuthRepository
import com.noljanolja.android.util.toDomainUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl private constructor(
    private val context: Context,
    private val googleWebClientId: String
) : AuthRepository {

    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance(REGION) }

    private val _userFlow = MutableStateFlow<User?>(null)
    override fun getCurrentUser() = _userFlow.asStateFlow()

    override suspend fun loginWithKakao(): Result<User> {
        val kakaoResult = getTokenKakao()
        return kakaoResult.getOrNull()?.let {
            val result = signInWithCustomToken(
                FirebaseFunction.Kakao,
                hashMapOf(KEY_TOKEN to it)
            )
            if (result.isSuccess) {
                Result.success(result.getOrNull()!!)
            } else {
                Log.e(TAG_AUTH_ERROR, result.exceptionOrNull()?.message.orEmpty())
                Result.failure(result.exceptionOrNull()!!)
            }
        } ?: Result.failure(kakaoResult.exceptionOrNull()!!)
    }

    override fun getGoogleSignInIntent(): Intent = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .build()
    ).signInIntent

    private suspend fun getTokenKakao(): Result<String> = suspendCoroutine { continuation ->
        val callback = { token: OAuthToken?, error: Throwable? ->
            token?.let {
                continuation.resume(Result.success(it.accessToken))
            } ?: continuation.resume(Result.failure(error!!))
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private suspend fun signInWithCustomToken(
        function: FirebaseFunction,
        data: HashMap<String, Any>
    ): Result<User> =
        try {
            val newToken = functions
                .getHttpsCallable(function.funcName)
                .call(data)
                .continueWith { task ->
                    val result = task.result?.data.toString()
                    result
                }.await()
            val authResult = FirebaseAuth.getInstance().signInWithCustomToken(newToken).await()
            Result.success(authResult.user.toDomainUser()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }

    companion object {
        const val REGION = "asia-northeast3"
        const val KEY_TOKEN = "token"
        const val TAG_AUTH_ERROR = "AUTH_ERROR"
        fun getInstance(
            context: Context,
            kakaoApiKey: String,
            googleWebClientId: String
        ) = AuthRepositoryImpl(
            context,
            googleWebClientId
        ).also {
            KakaoSdk.init(context, kakaoApiKey)
        }
    }
}

enum class FirebaseFunction(val funcName: String) {
    Kakao("api/auth/kakao"),
    Naver("api/auth/naver"),
}
