package com.d2brothers.firebase_auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.d2brothers.firebase_auth.model.AuthUser
import com.d2brothers.firebase_auth.utils.toAuthUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class Auth private constructor(
    private val context: Context,
) : AuthInterface {

    private val authConfig: AuthConfig by lazy { AuthConfig.instance }

    private val firebaseAuth by lazy { Firebase.auth }

    private val functions: FirebaseFunctions by lazy {
        authConfig.region?.let {
            FirebaseFunctions.getInstance(
                it,
            )
        } ?: FirebaseFunctions.getInstance()
    }

    fun getCurrentUser(reload: Boolean = false): Flow<AuthUser?> = flow {
        emit(firebaseAuth.currentUser?.toAuthUser())
    }.onStart {
        if (reload) firebaseAuth.currentUser?.reload()
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<AuthUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!.toAuthUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<AuthUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user?.toAuthUser()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        firebaseAuth.sendPasswordResetEmail(email).await()
        return Result.success(true)
    }

    suspend fun sendEmailVerification(): Result<Boolean> {
        firebaseAuth.currentUser?.sendEmailVerification()?.await()
        return Result.success(true)
    }

    fun logOut(): Result<Boolean> {
        firebaseAuth.signOut()
        return Result.success(firebaseAuth.currentUser == null)
    }

    suspend fun loginWithKakao(): Result<AuthUser> {
        val kakaoResult = getTokenKakao()
        return kakaoResult.getOrNull()?.let {
            val result = signInWithCustomToken(
                FirebaseFunction.Kakao,
                hashMapOf(KEY_TOKEN to it),
            )
            if (result.isSuccess) {
                Result.success(result.getOrNull()!!)
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } ?: Result.failure(kakaoResult.exceptionOrNull()!!)
    }

    fun authenticateGoogle(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val intent = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(authConfig.googleClientId)
                .requestEmail()
                .build(),
        ).signInIntent
        launcher.launch(intent)
    }

    suspend fun getAccountFromGoogleIntent(data: Intent?): Result<AuthUser> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            getCurrentUser().first().let {
                Result.success(it!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun authenticateNaver(context: Context, launcher: ActivityResultLauncher<Intent>) {
        NaverIdLoginSDK.authenticate(context, launcher)
    }

    suspend fun getAccountFromNaverIntent(data: Intent?): Result<AuthUser> {
        val accessToken = NaverIdLoginSDK.getAccessToken()
        return accessToken?.let {
            loginWithNaverToken(it)
        } ?: Result.failure(Exception(NaverIdLoginSDK.getLastErrorDescription()))
    }

    suspend fun getIdToken(forceRefresh: Boolean): String? {
        return firebaseAuth.currentUser?.getIdToken(forceRefresh)?.await()?.token
    }

    private suspend fun loginWithNaverToken(token: String): Result<AuthUser> {
        val result = signInWithCustomToken(
            FirebaseFunction.Naver,
            hashMapOf(KEY_TOKEN to token),
        )
        return if (result.isSuccess) {
            Result.success(result.getOrNull()!!)
        } else {
            Result.failure(result.exceptionOrNull()!!)
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

    private suspend fun signInWithCustomToken(
        function: FirebaseFunction,
        data: HashMap<String, Any>,
    ): Result<AuthUser> = try {
        val newToken =
            functions.getHttpsCallable(function.funcName).call(data).continueWith { task ->
                val result = task.result?.data.toString()
                result
            }.await()
        val authResult = firebaseAuth.signInWithCustomToken(newToken).await()
        Result.success(authResult.user!!.toAuthUser())
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        private const val KEY_TOKEN = "token"
        lateinit var instance: Auth
        fun init(context: Context) {
            instance = Auth(context)
        }
    }
}
