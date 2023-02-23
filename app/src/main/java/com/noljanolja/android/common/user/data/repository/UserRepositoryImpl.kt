package com.noljanolja.android.common.user.data.repository

import android.util.Log
import com.noljanolja.android.common.user.data.datasource.UserRemoteDataSource
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository

class UserRepositoryImpl(private val userRemoteDataSource: UserRemoteDataSource) : UserRepository {
    private var _currentUser: User? = null
    override fun getCurrentUser(): User? = _currentUser

    override suspend fun sendRegistrationToServer(token: String) {
        // TODO
        Log.e("USER_REPOSITORY", "SEND$token")
    }

    override suspend fun getMe(): Result<User> {
        return userRemoteDataSource.getMe().also {
            _currentUser = it.getOrNull()
        }
    }
}
