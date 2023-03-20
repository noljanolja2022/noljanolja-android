package com.d2brothers.firebase_auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import java.util.*

class AuthSdk private constructor(private val context: Context) {
    internal val auth: Auth by lazy { Auth(context) }

    // Phone
    suspend fun verifyOTPCode(
        verificationId: String,
        code: String,
    ): Result<String> = auth.verifyOTPCode(verificationId, code)

    // Email
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String> = auth.createUserWithEmailAndPassword(email, password)

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String> = auth.signInWithEmailAndPassword(email, password)

    suspend fun sendPasswordResetEmail(
        email: String,
    ): Result<Boolean> = auth.sendPasswordResetEmail(email)

    suspend fun sendEmailVerification(): Result<Boolean> = auth.sendEmailVerification()

    // Kakao
    suspend fun loginWithKakao(): Result<String> = auth.loginWithKakao()

    // Google
    suspend fun getAccountFromGoogleIntent(
        data: Intent?,
    ): Result<String> = auth.getAccountFromGoogleIntent(data)

    // Naver
    suspend fun getAccountFromNaverIntent(
        data: Intent?,
    ): Result<String> = auth.getAccountFromNaverIntent(data)

    suspend fun getIdToken(forceRefresh: Boolean): String? {
        return auth.getIdToken(forceRefresh)
    }

    // update
    suspend fun updateUser(
        name: String,
        photo: String?,
    ): Result<Unit> = auth.updateUser(name, photo)

    // Logout
    fun logOut(): Result<Boolean> = auth.logOut()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: AuthSdk
        fun init(
            context: Context,
            kakaoApiKey: String,
            naverClientId: String,
            naverClientSecret: String,
            naverClientName: String,
            googleWebClientId: String,
            region: String?,
        ): AuthSdk {
//            KakaoSdk.init(context, kakaoApiKey)
//            NaverIdLoginSDK.initialize(
//                context,
//                naverClientId,
//                naverClientSecret,
//                naverClientName,
//            )
            Firebase.auth.setLanguageCode(Locale.getDefault().language)
            AuthConfig.init(region = region, googleClientId = googleWebClientId)
            return AuthSdk(context).also {
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

        fun loginWithPhone(
            context: Activity,
            phone: String,
            timeout: Long,
            onVerificationCompleted: (String?) -> Unit,
            onError: (Exception) -> Unit,
            onCodeSent: (String) -> Unit,
        ) {
            instance.auth.loginWithPhone(
                context = context,
                phone = phone,
                timeout = timeout,
                onVerificationCompleted = onVerificationCompleted,
                onError = onError,
                onCodeSent = onCodeSent,
            )
        }
    }
}

enum class FirebaseFunction(val funcName: String) {
    Kakao("api/auth/kakao"), Naver("api/auth/naver"),
}
