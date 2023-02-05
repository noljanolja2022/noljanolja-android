package com.noljanolja.android.data.repositories

import android.content.Intent
import com.noljanolja.android.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun getCurrentUser(): StateFlow<User?>

    suspend fun loginWithKakao(): Result<User>

    fun getGoogleSignInIntent(): Intent

}