package com.noljanolja.android.common.auth.domain.repository

import android.content.Intent
import com.noljanolja.android.common.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>

    suspend fun loginWithKakao(): Result<User>

    fun getGoogleSignInIntent(): Intent

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean>

    suspend fun loginWithNaver(token: String): Result<User>

    fun logOut(): Result<Boolean>
}
