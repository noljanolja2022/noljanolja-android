package com.noljanolja.android.features.auth.signup.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.features.auth.common.component.EmailAndPassword

@Composable
fun SignupScreen() {
    val signupViewModel: SignupViewModel = hiltViewModel()
    val email by signupViewModel.emailFlow.collectAsState()
    val password by signupViewModel.passwordFlow.collectAsState()
    SignupContent(
        modifier = Modifier.fillMaxSize(),
        email = email,
        password = password,
        onEmailChange = { signupViewModel.changeEmail(it) },
        onPasswordChange = { signupViewModel.changePassword(it) },
        onSubmit = { signupViewModel.onSignup() }
    )
}

@Composable
private fun SignupContent(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = modifier) {
        EmailAndPassword(
            email = email,
            password = password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange
        )
        Button(onClick = onSubmit) {
            Text("Signup")
        }
    }
}
