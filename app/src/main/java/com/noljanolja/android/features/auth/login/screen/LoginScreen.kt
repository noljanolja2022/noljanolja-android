package com.noljanolja.android.features.auth.login.screen

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
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
    val email by viewModel.emailFlow.collectAsState()
    val password by viewModel.passwordFlow.collectAsState()
    LoginContent(
        email = email,
        password = password,
        onEmailChange = { viewModel.changeEmail(it) },
        onPasswordChange = { viewModel.changePassword(it) },
        onLoginGoogle = {
            launcher.launch(viewModel.getGoogleIntent())
        },
        onLoginKakao = {
            viewModel.loginWithKakao()
        },
        onSignup = {
            viewModel.goToSignup()
        },
        onLoginWithEmailAndPassword = {
            viewModel.signInWithEmailAndPassword()
        }
    )
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginGoogle: () -> Unit,
    onLoginKakao: () -> Unit,
    onSignup: () -> Unit,
    onLoginWithEmailAndPassword: () -> Unit
) {
    Column(modifier = modifier) {
        EmailAndPassword(
            email = email,
            password = password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange
        )
        Button(onClick = { onLoginWithEmailAndPassword() }) {
            Text(text = "Login")
        }
        Text(
            text = "Don't have account? Signup",
            modifier = Modifier.clickable {
                onSignup()
            }
        )
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
