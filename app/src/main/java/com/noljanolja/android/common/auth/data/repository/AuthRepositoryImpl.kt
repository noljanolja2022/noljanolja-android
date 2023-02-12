package com.noljanolja.android.common.auth.data.repository

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
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.noljanolja.android.common.auth.domain.model.User
import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.util.toDomainUser
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AuthRepositoryImpl private constructor(
    private val context: Context,
    private val googleWebClientId: String
) : AuthRepository {
    private val _firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance(REGION) }

    override fun getCurrentUser() = flow { emit(_firebaseAuth.currentUser.toDomainUser()) }

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

    override suspend fun loginWithNaver(token: String): Result<User> {
        val result = signInWithCustomToken(
            FirebaseFunction.Naver,
            hashMapOf(KEY_TOKEN to token)
        )
        if (result.isSuccess) {
            return Result.success(result.getOrNull()!!)
        } else {
            Log.e(TAG_AUTH_ERROR, result.exceptionOrNull()?.message.orEmpty())
            return Result.failure(result.exceptionOrNull()!!)
        }

    }


    override fun getGoogleSignInIntent(): Intent = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId).requestEmail().build()
    ).signInIntent

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val authResult = _firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(authResult.user.toDomainUser()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val authResult = _firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user.toDomainUser()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    private suspend fun getTokenNaver(): Result<String> = suspendCoroutine { continuation ->
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NaverIdLoginSDK.getAccessToken()?.let {
                    continuation.resume(Result.success(it))
                } ?: continuation.resume(Result.failure(Throwable("Cannot get access token")))
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                continuation.resume(Result.failure(Throwable(errorDescription)))
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
                continuation.resume(Result.failure(Throwable(message)))
            }
        }
        NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
    }

    private suspend fun signInWithCustomToken(
        function: FirebaseFunction,
        data: HashMap<String, Any>
    ): Result<User> = try {
        val newToken =
            functions.getHttpsCallable(function.funcName).call(data).continueWith { task ->
                val result = task.result?.data.toString()
                result
            }.await()
        val authResult = _firebaseAuth.signInWithCustomToken(newToken).await()
        Result.success(authResult.user.toDomainUser()!!)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        _firebaseAuth.sendPasswordResetEmail(email).await()
        return Result.success(true)
    }

    override fun logOut(): Result<Boolean> {
        _firebaseAuth.signOut()
        return Result.success(_firebaseAuth.currentUser == null)
    }

    companion object {
        const val REGION = "asia-northeast3"
        const val KEY_TOKEN = "token"
        const val TAG_AUTH_ERROR = "AUTH_ERROR"
        fun getInstance(
            context: Context,
            kakaoApiKey: String,
            naver_client_id: String,
            naver_client_secret: String,
            naver_client_name: String,
            googleWebClientId: String
        ) = AuthRepositoryImpl(
            context,
            googleWebClientId
        ).also {
            KakaoSdk.init(context, kakaoApiKey)
            NaverIdLoginSDK.initialize(
                context,
                naver_client_id,
                naver_client_secret,
                naver_client_name
            )
        }
    }
}

enum class FirebaseFunction(val funcName: String) {
    Kakao("api/auth/kakao"), Naver("api/auth/naver"),
}
