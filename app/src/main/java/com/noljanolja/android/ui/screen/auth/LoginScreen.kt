package com.noljanolja.android.ui.screen.auth

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.util.loginWithGoogle
import com.noljanolja.android.util.loginWithKakao
import com.noljanolja.android.util.rememberFirebaseAuthLauncher

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
            viewModel.handleEvent(LoginEvent.GoToMain)
        }, onAuthError = {
            // TODO
        })
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            loginWithGoogle(context, launcher)
        }) {
            Text("Login Google")
        }
        Button(onClick = {
            loginWithKakao(context, onAuthComplete = {
                viewModel.handleEvent(LoginEvent.GoToMain)
            }, onAuthError = {
                Log.e("KAKAO_ERROR", it.message.toString())

            })

        }) {
            Text("Login Kakao")
        }
    }
}

