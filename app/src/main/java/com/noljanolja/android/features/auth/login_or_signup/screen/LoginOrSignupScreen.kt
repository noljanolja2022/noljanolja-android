package com.noljanolja.android.features.auth.login_or_signup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.common.composable.TwoButtonInRow
import com.noljanolja.android.common.navigation.NavigationCommand.FinishWithResults.Companion.FORGOT_FINISH_AUTH
import com.noljanolja.android.features.auth.common.component.FullSizeWithLogo
import com.noljanolja.android.features.auth.login.screen.LoginScreen
import com.noljanolja.android.features.auth.login.screen.LoginUIState
import com.noljanolja.android.features.auth.login.screen.LoginViewModel
import com.noljanolja.android.features.auth.signup.screen.SignupScreen
import com.noljanolja.android.features.auth.signup.screen.SignupUIState
import com.noljanolja.android.features.auth.signup.screen.SignupViewModel

@Composable
fun LoginOrSignupScreen(
    savedStateHandle: SavedStateHandle
) {
    val loginOrSignupViewModel: LoginOrSignupViewModel = hiltViewModel()
    val uiState by loginOrSignupViewModel.uiStateFlow.collectAsState()
    LoginOrSignupContent(uiState = uiState) {
        loginOrSignupViewModel.handleEvent(it)
    }
    val closeAuth = savedStateHandle.get<Boolean>(FORGOT_FINISH_AUTH) ?: false
    LaunchedEffect(key1 = closeAuth) {
        if (closeAuth) {
            savedStateHandle.remove<Boolean>(FORGOT_FINISH_AUTH)
            loginOrSignupViewModel.handleEvent(LoginOrSignupEvent.Close)
        }
    }
}

@Composable
fun LoginOrSignupContent(
    modifier: Modifier = Modifier,
    uiState: LoginOrSignupUIState,
    handleEvent: (LoginOrSignupEvent) -> Unit
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val signupViewModel: SignupViewModel = hiltViewModel()
    val loginUIState by loginViewModel.uiStateFlow.collectAsState()
    val signupUIState by signupViewModel.uiStateFlow.collectAsState()
    FullSizeLoading(loginUIState == LoginUIState.Loading || (signupUIState as? SignupUIState.SignupForm)?.isLoading == true) {
        FullSizeWithLogo(
            onBack = {
                handleEvent(LoginOrSignupEvent.Close)
            }
        ) {
            Card(
                modifier = modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 20.dp)
                        .padding(top = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        TwoButtonInRow(
                            modifier = Modifier.fillMaxWidth(),
                            fModifier = Modifier.weight(1F),
                            sModifier = Modifier.weight(2F),
                            firstText = stringResource(id = R.string.login),
                            secondText = stringResource(id = R.string.signup),
                            indexFocused = uiState.index,
                            firstClick = {
                                handleEvent(LoginOrSignupEvent.SwitchToLogin)
                            },
                            secondClick = {
                                handleEvent(LoginOrSignupEvent.SwitchSignup)
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        when (uiState) {
                            LoginOrSignupUIState.Login -> LoginScreen(viewModel = loginViewModel)
                            LoginOrSignupUIState.Signup -> SignupScreen(signupViewModel = signupViewModel)
                        }
                    }
                )
            }
        }
    }
}
