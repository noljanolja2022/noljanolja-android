package com.noljanolja.android.domain.repositories

import android.content.Intent
import com.noljanolja.android.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun getCurrentUser(): StateFlow<User?>

    suspend fun loginWithKakao(): Result<User>

    fun getGoogleSignInIntent(): Intent

}