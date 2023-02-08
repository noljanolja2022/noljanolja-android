package com.noljanolja.android.features.home.screen

import androidx.lifecycle.ViewModel
import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun logOut() {
        launch {
            val result = authRepository.logOut()
            if (result.isSuccess) {
                navigationManager.navigate(NavigationDirections.Login)
            }
        }
    }
}
