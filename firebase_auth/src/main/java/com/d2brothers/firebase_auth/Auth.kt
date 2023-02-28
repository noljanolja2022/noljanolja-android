package com.d2brothers.firebase_auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class Auth(
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

    // Phone
    fun loginWithPhone(
        context: Activity,
        phone: String,
        timeout: Long,
        onVerificationCompleted: (String?) -> Unit,
        onError: (Exception) -> Unit,
        onCodeSent: (String) -> Unit,
    ) {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phone)
            .setTimeout(timeout, TimeUnit.SECONDS)
            .setActivity(context)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(
                    credential: PhoneAuthCredential,
                ) {
                    onVerificationCompleted(credential.smsCode)
                }

                override fun onVerificationFailed(
                    exception: FirebaseException,
                ) {
                    onError(exception)
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken,
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyOTPCode(
        verificationId: String,
        code: String,
    ): Result<String> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            Firebase.auth.signInWithCredential(credential).await()
            getIdToken().let {
                Result.success(it!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Email
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            getIdToken().let {
                Result.success(it!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<String> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            getIdToken().let {
                Result.success(it!!)
            }
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

    // Kakao
    suspend fun loginWithKakao(): Result<String> {
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

    // Google
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

    suspend fun getAccountFromGoogleIntent(data: Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            getIdToken().let {
                Result.success(it!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Naver
    fun authenticateNaver(context: Context, launcher: ActivityResultLauncher<Intent>) {
        NaverIdLoginSDK.authenticate(context, launcher)
    }

    suspend fun getAccountFromNaverIntent(data: Intent?): Result<String> {
        val accessToken = NaverIdLoginSDK.getAccessToken()
        return accessToken?.let {
            loginWithNaverToken(it)
        } ?: Result.failure(Exception(NaverIdLoginSDK.getLastErrorDescription()))
    }

    private suspend fun loginWithNaverToken(token: String): Result<String> {
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

    // Get token
    suspend fun getIdToken(forceRefresh: Boolean = false): String? {
        if (forceRefresh) firebaseAuth.currentUser?.reload()?.await()
        return firebaseAuth.currentUser?.getIdToken(forceRefresh)?.await()?.token
    }

    suspend fun updateUser(
        name: String,
        photo: String?,
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        firebaseAuth.currentUser?.let { user ->
            val profileUpdates = userProfileChangeRequest {
                displayName = name
                photoUri = photo?.let { Uri.parse(it) }
            }
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(
                            Result.failure(
                                task.exception ?: Throwable("UnknownError")
                            )
                        )
                    }
                }.addOnFailureListener {
                    continuation.resume(
                        Result.failure(it)
                    )
                }
        } ?: continuation.resume(Result.failure(Throwable("Cannot get user")))
    }

    private suspend fun signInWithCustomToken(
        function: FirebaseFunction,
        data: HashMap<String, Any>,
    ): Result<String> = try {
        val newToken =
            functions.getHttpsCallable(function.funcName).call(data).continueWith { task ->
                val result = task.result?.data.toString()
                result
            }.await()
        firebaseAuth.signInWithCustomToken(newToken).await()
        getIdToken().let {
            Result.success(it!!)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Logout
    fun logOut(): Result<Boolean> {
        firebaseAuth.signOut()
        return Result.success(firebaseAuth.currentUser == null)
    }

    companion object {
        private const val KEY_TOKEN = "token"
    }
}
