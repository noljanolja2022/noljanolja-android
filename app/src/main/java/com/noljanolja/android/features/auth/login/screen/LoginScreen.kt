package com.noljanolja.android.features.auth.login.screen

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.composable.PrimaryButton
import com.noljanolja.android.common.composable.SecondaryButton
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.common.component.VerifyEmail
import com.noljanolja.android.features.auth.login.screen.component.LoginButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    viewModel.handleError()
    val email by viewModel.emailFlow.collectAsState()
    val password by viewModel.passwordFlow.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val googleLauncher = rememberAuthLauncher {
        viewModel.handleLoginWithGoogleFromIntent(it)
    }
    val naverLauncher = rememberAuthLauncher {
        viewModel.handleLoginWithNaverFromIntent(it)
    }
    val uiState by viewModel.uiStateFlow.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            LoginUIState.VerifyEmail -> {
                LoginVerifyEmail(
                    onBack = {
                        viewModel.handleEvent(LoginEvent.Back)
                    },
                    onVerify = {
                        viewModel.handleEvent(LoginEvent.VerifyEmail)
                    },
                )
            }
            else -> {
                LoginContent(
                    email = email,
                    password = password,
                    emailError = emailError,
                    passwordError = passwordError,
                    handleEvent = {
                        viewModel.handleEvent(it)
                    },
                    onLoginGoogle = {
                        AuthSdk.authenticateGoogle(context, googleLauncher)
                    },
                    onLoginNaver = {
                        AuthSdk.authenticateNaver(context, naverLauncher)
                    },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.LoginContent(
    email: String,
    password: String,
    emailError: Throwable?,
    passwordError: Throwable?,
    handleEvent: (LoginEvent) -> Unit,
    onLoginGoogle: () -> Unit,
    onLoginNaver: () -> Unit,
) {
    EmailAndPassword(
        email = email,
        password = password,
        emailError = emailError,
        passwordError = passwordError,
        onEmailChange = {
            handleEvent(LoginEvent.ChangeEmail(it))
        },
        onPasswordChange = {
            handleEvent(LoginEvent.ChangePassword(it))
        },
    )
    Text(
        text = stringResource(id = R.string.forgot_password),
        style = TextStyle(
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
            .padding(top = 28.dp)
            .align(Alignment.End)
            .clickable {
                handleEvent(LoginEvent.GoForgotEmailAndPassword)
            },
    )
    LoginButton(
        modifier = Modifier.padding(top = 28.dp),
        isEnable = email.isNotBlank() && password.isNotBlank(),
    ) {
        handleEvent(LoginEvent.LoginEmail)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp,
            modifier = Modifier.weight(1F),
        )
        Text(
            stringResource(id = R.string.auth_login_with_SNS),
            modifier = Modifier.padding(24.dp),
            style = TextStyle(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
            ),
        )
        Divider(
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp,
            modifier = Modifier.weight(1F),
        )
    }

    Row {
        LoginSNSButton(painter = painterResource(id = R.drawable.kakao), onClick = {
            handleEvent(LoginEvent.LoginKakao)
        })
        Spacer(modifier = Modifier.width(24.dp))
        LoginSNSButton(painter = painterResource(id = R.drawable.naver), onClick = onLoginNaver)
        Spacer(modifier = Modifier.width(24.dp))
        LoginSNSButton(
            painter = painterResource(id = R.drawable.google),
            onClick = onLoginGoogle,
        )
    }
    Spacer(modifier = Modifier.weight(1F))
}

@Composable
fun ColumnScope.LoginVerifyEmail(
    onBack: () -> Unit,
    onVerify: () -> Unit,
) {
    VerifyEmail()
    Row() {
        SecondaryButton(
            modifier = Modifier.weight(1F),
            text = stringResource(id = R.string.common_previous),
            onClick = onBack,
        )
        Spacer(modifier = Modifier.width(12.dp))
        PrimaryButton(
            modifier = Modifier.weight(1F),
            text = stringResource(id = R.string.common_verification),
            onClick = onVerify,
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun rememberAuthLauncher(
    handleAuthResult: (Intent?) -> Unit,
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleAuthResult(result.data)
    }
}

@Composable
private fun LoginSNSButton(
    painter: Painter,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(0.dp, Color.Transparent),
    ) {
        Image(
            painter = painter,
            modifier = Modifier.size(42.dp),
            contentDescription = null,
        )
    }
}
