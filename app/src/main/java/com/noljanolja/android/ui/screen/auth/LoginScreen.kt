package com.noljanolja.android.ui.screen.auth

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val launcher = rememberFirebaseAuthLauncher {
        viewModel.handleLoginGoogleResult(GoogleSignIn.getSignedInAccountFromIntent(it))
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            launcher.launch(viewModel.getGoogleIntent())
        }) {
            Text("Login Google")
        }
        Button(onClick = { viewModel.loginWithKakao() }) {
            Text("Login Kakao")
        }
    }
}

@Composable
private fun rememberFirebaseAuthLauncher(
    handleGoogleSignInResult: (Intent?) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGoogleSignInResult(result.data)
    }
}
