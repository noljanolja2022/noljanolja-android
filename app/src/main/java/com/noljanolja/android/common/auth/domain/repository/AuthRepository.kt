package com.noljanolja.android.common.auth.domain.repository

import android.content.Intent
import com.noljanolja.android.common.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>

    suspend fun loginWithKakao(): Result<User>

    fun getGoogleSignInIntent(): Intent

}