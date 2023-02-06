package com.noljanolja.android.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _openMainEvent = MutableSharedFlow<FirebaseUser?>()
    val openMainEvent = _openMainEvent.asSharedFlow()

    private val auth: FirebaseAuth = Firebase.auth

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _openMainEvent.emit(auth.currentUser)
        }
    }
}