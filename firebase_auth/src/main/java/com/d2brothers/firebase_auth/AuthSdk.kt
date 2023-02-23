package com.d2brothers.firebase_auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.d2brothers.firebase_auth.model.AuthUser
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.flow.Flow

class AuthSdk private constructor() {
    internal val auth: Auth by lazy { Auth.instance }

    fun getCurrentUser(reload: Boolean = false): Flow<AuthUser?> = auth.getCurrentUser(reload)

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<AuthUser> = auth.createUserWithEmailAndPassword(email, password)

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<AuthUser> = auth.signInWithEmailAndPassword(email, password)

    suspend fun sendPasswordResetEmail(
        email: String,
    ): Result<Boolean> = auth.sendPasswordResetEmail(email)

    suspend fun sendEmailVerification(): Result<Boolean> = auth.sendEmailVerification()

    fun logOut(): Result<Boolean> = auth.logOut()

    suspend fun loginWithKakao(): Result<AuthUser> = auth.loginWithKakao()

    suspend fun getAccountFromGoogleIntent(
        data: Intent?,
    ): Result<AuthUser> = auth.getAccountFromGoogleIntent(data)

    suspend fun getAccountFromNaverIntent(
        data: Intent?,
    ): Result<AuthUser> = auth.getAccountFromNaverIntent(data)

    suspend fun getIdToken(forceRefresh: Boolean): String? {
        return auth.getIdToken(forceRefresh)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: AuthSdk
        fun init(
            context: Context,
            kakaoApiKey: String,
            naver_client_id: String,
            naver_client_secret: String,
            naver_client_name: String,
            googleWebClientId: String,
            region: String?,
        ): AuthSdk {
            KakaoSdk.init(context, kakaoApiKey)
            NaverIdLoginSDK.initialize(
                context,
                naver_client_id,
                naver_client_secret,
                naver_client_name,
            )
            AuthConfig.init(region = region, googleClientId = googleWebClientId)
            Auth.init(context)
            return AuthSdk().also {
                instance = it
            }
        }

        fun authenticateGoogle(
            context: Context,
            launcher: ActivityResultLauncher<Intent>,
        ) {
            instance.auth.authenticateGoogle(context, launcher)
        }

        fun authenticateNaver(
            context: Context,
            launcher: ActivityResultLauncher<Intent>,
        ) {
            instance.auth.authenticateNaver(context, launcher)
        }
    }
}

enum class FirebaseFunction(val funcName: String) {
    Kakao("api/auth/kakao"), Naver("api/auth/naver"),
}
