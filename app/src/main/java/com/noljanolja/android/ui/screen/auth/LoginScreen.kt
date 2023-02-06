package com.noljanolja.android.ui.screen.auth

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.noljanolja.android.util.showToast

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.errorFlow) {
        viewModel.errorFlow.collect {
            context.showToast(it.message)
        }
    }
    val launcher = rememberFirebaseAuthLauncher {
        viewModel.handleLoginGoogleResult(GoogleSignIn.getSignedInAccountFromIntent(it))
    }
    LoginContent(
        onLoginGoogle = {
            launcher.launch(viewModel.getGoogleIntent())
        },
        onLoginKakao = {
            viewModel.loginWithKakao()
        }
    )
}

@Composable
fun LoginContent(
    onLoginGoogle: () -> Unit,
    onLoginKakao: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onLoginGoogle) {
            Text("Login Google")
        }
        Button(onClick = onLoginKakao) {
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
