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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.login.screen.component.LoginButton
import com.noljanolja.android.util.getErrorMessage

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    viewModel.handleError()

    val uiState by viewModel.uiStateFlow.collectAsState()
    val email by viewModel.emailFlow.collectAsState()
    val password by viewModel.passwordFlow.collectAsState()
    val error by viewModel.errorLoginEmailPassword.collectAsState()
    val launcher = rememberFirebaseAuthLauncher {
        viewModel.handleLoginGoogleResult(GoogleSignIn.getSignedInAccountFromIntent(it))
    }
    FullSizeLoading(
        showLoading = uiState is LoginUIState.Loading
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                email = email,
                password = password,
                error = error,
                handleEvent = {
                    viewModel.handleEvent(it)
                }, onLoginGoogle = {
                    launcher.launch(viewModel.getGoogleIntent())

                }
            )
        }
    }
}

@Composable
private fun ColumnScope.LoginContent(
    email: String,
    password: String,
    handleEvent: (LoginEvent) -> Unit,
    onLoginGoogle: () -> Unit,
    error: Throwable?,
) {
    val context = LocalContext.current
    EmailAndPassword(email = email, password = password, onEmailChange = {
        handleEvent(LoginEvent.ChangeEmail(it))
    }, onPasswordChange = {
        handleEvent(LoginEvent.ChangePassword(it))
    })
    error?.let {
        Text(
            context.getErrorMessage(it),
            modifier = Modifier
                .padding(start = 24.dp, top = 12.dp)
                .align(Alignment.Start),
            style = TextStyle(
                color = colorResource(id = R.color.error_text_color), fontSize = 14.sp
            )
        )
    }
    Text(text = stringResource(id = R.string.forgot_password), style = TextStyle(
        fontSize = 14.sp, color = colorResource(id = R.color.secondary_text_color)
    ), modifier = Modifier
        .padding(top = 28.dp)
        .align(Alignment.End)
        .clickable {
            handleEvent(LoginEvent.GoForgotEmailAndPassword)
        })
    LoginButton(
        modifier = Modifier.padding(top = 28.dp),
        isEnable = email.isNotBlank() && password.isNotBlank()
    ) {
        handleEvent(LoginEvent.LoginEmail)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(
            color = colorResource(id = R.color.border_color),
            thickness = 1.dp,
            modifier = Modifier.weight(1F)
        )
        Text(
            stringResource(id = R.string.auth_login_with_SNS),
            modifier = Modifier.padding(24.dp),
            style = TextStyle(
                fontSize = 12.sp, color = colorResource(id = R.color.secondary_text_color)
            )
        )
        Divider(
            color = colorResource(id = R.color.border_color),
            thickness = 1.dp,
            modifier = Modifier.weight(1F)
        )
    }

    Row {
        LoginSNSButton(painter = painterResource(id = R.drawable.kakao), onClick = {
            handleEvent(LoginEvent.LoginKakao)
        })
        Spacer(modifier = Modifier.width(24.dp))
        LoginSNSButton(painter = painterResource(id = R.drawable.naver), onClick = {
            handleEvent(LoginEvent.LoginNaver)
        })
        Spacer(modifier = Modifier.width(24.dp))
        LoginSNSButton(
            painter = painterResource(id = R.drawable.google), onClick = onLoginGoogle
        )
    }
    Spacer(modifier = Modifier.weight(1F))
}

@Composable
private fun rememberFirebaseAuthLauncher(
    handleGoogleSignInResult: (Intent?) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGoogleSignInResult(result.data)
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
            painter = painter, modifier = Modifier.size(42.dp), contentDescription = null
        )
    }
}
