package com.noljanolja.android.features.auth.signup.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.composable.RoundedButton
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.common.component.RoundedTextField

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = hiltViewModel(),
) {
    signupViewModel.handleError()
    val uiState by signupViewModel.uiStateFlow.collectAsState()
    val email by signupViewModel.emailFlow.collectAsState()
    val password by signupViewModel.passwordFlow.collectAsState()
    val confirmPassword by signupViewModel.confirmPasswordFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SignupContent(
            email,
            password,
            confirmPassword,
        ) {
            signupViewModel.handleEvent(it)
        }
    }
}

@Composable
private fun ColumnScope.SignupContent(
    email: String,
    password: String,
    confirmPassword: String,
    handleEvent: (SignupEvent) -> Unit,
) {

    EmailAndPassword(
        email = email,
        password = password,
        onEmailChange = {
            handleEvent(SignupEvent.ChangeEmail(it))
        },
        onPasswordChange = {
            handleEvent(SignupEvent.ChangePassword(it))
        }
    )
    Spacer(modifier = Modifier.height(12.dp))
    RoundedTextField(
        value = confirmPassword,
        hint = stringResource(id = R.string.confirm_password_hint_text),
        hideText = true,
        onValueChange = {
            handleEvent(SignupEvent.ChangeConfirmPassword(it))
        }
    )
    Spacer(modifier = Modifier.weight(1F))
    RoundedButton(
        text = stringResource(id = R.string.signup),
        isEnable = isEnableSignup(email, password, confirmPassword),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary_text_color),
            disabledContainerColor = colorResource(id = R.color.background),
            contentColor = Color.White,
            disabledContentColor = colorResource(id = R.color.disable_text)
        ),
        onClick = {
            handleEvent(SignupEvent.Signup)
        }
    )
    Spacer(modifier = Modifier.height(24.dp))
}

private fun isEnableSignup(
    email: String,
    password: String,
    confirmPassword: String
) = email.isNotBlank() && password.isNotBlank() && password == confirmPassword

