package com.noljanolja.android.features.auth.login.screen

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.common.composable.TwoButtonInRow
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.login.screen.component.LoginButton
import com.noljanolja.android.util.getErrorMessage
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
    val uiState by viewModel.uiStateFlow.collectAsState()
    val error by viewModel.errorLoginEmailPassword.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryColor)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1F))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .width(166.dp)
                .height(66.dp)
        )
        Spacer(modifier = Modifier.weight(1F))
        LoginContent(
            email = email,
            password = password,
            loginEmailError = error?.let { context.getErrorMessage(it) },
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .background(Color.White)
        )
    }

    if (uiState is LoginUIState.Loading) {
        FullSizeLoading()
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    loginEmailError: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginGoogle: () -> Unit,
    onLoginKakao: () -> Unit,
    onSignup: () -> Unit,
    onLoginWithEmailAndPassword: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            TwoButtonInRow(
                modifier = Modifier.fillMaxWidth(),
                fModifier = Modifier.weight(1F),
                sModifier = Modifier.weight(2F),
                firstText = stringResource(id = R.string.login),
                secondText = stringResource(id = R.string.signup),
                indexFocused = 0,
                firstClick = { },
                secondClick = {})
            Spacer(modifier = Modifier.height(24.dp))
            EmailAndPassword(
                email = email,
                password = password,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange
            )
            loginEmailError?.let {
                Text(
                    it,
                    modifier = Modifier
                        .padding(start = 24.dp, top = 12.dp)
                        .align(Alignment.Start),
                    style = TextStyle(
                        color = colorResource(id = R.color.error_text_color),
                        fontSize = 14.sp
                    )
                )
            }
            Text(
                text = stringResource(id = R.string.forgot_password),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_text_color)
                ),
                modifier = Modifier
                    .padding(top = 28.dp)
                    .align(Alignment.End)
                    .clickable {
                        onSignup()
                    }
            )
            LoginButton(
                modifier = Modifier.padding(top = 28.dp),
                isEnable = email.isNotBlank() && password.isNotBlank()
            ) {
                onLoginWithEmailAndPassword()
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
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.secondary_text_color)
                    )
                )
                Divider(
                    color = colorResource(id = R.color.border_color),
                    thickness = 1.dp,
                    modifier = Modifier.weight(1F)
                )
            }

            Row {
                LoginSNSButton(
                    painter = painterResource(id = R.drawable.kakao),
                    onClick = onLoginKakao
                )
                Spacer(modifier = Modifier.width(24.dp))
                LoginSNSButton(
                    painter = painterResource(id = R.drawable.naver),
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(24.dp))
                LoginSNSButton(
                    painter = painterResource(id = R.drawable.google),
                    onClick = onLoginGoogle
                )
            }
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
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}