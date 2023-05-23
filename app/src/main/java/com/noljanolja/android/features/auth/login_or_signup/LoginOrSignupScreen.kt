package com.noljanolja.android.features.auth.login_or_signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.common.navigation.NavigationCommand.FinishWithResults.Companion.FORGOT_FINISH_AUTH
import com.noljanolja.android.features.auth.common.component.FullSizeWithLogo
import com.noljanolja.android.features.auth.login.LoginScreen
import com.noljanolja.android.features.auth.login.LoginUIState
import com.noljanolja.android.features.auth.login.LoginViewModel
import com.noljanolja.android.features.auth.signup.SignupScreen
import com.noljanolja.android.features.auth.signup.SignupUIState
import com.noljanolja.android.features.auth.signup.SignupViewModel
import com.noljanolja.android.ui.composable.FullSizeLoading
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginOrSignupScreen(
    savedStateHandle: SavedStateHandle,
) {
    val loginOrSignupViewModel: LoginOrSignupViewModel = getViewModel()
    val uiState by loginOrSignupViewModel.uiStateFlow.collectAsStateWithLifecycle()
    LoginOrSignupContent(uiState = uiState, savedStateHandle = savedStateHandle) {
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
    savedStateHandle: SavedStateHandle,
    uiState: LoginOrSignupUIState,
    handleEvent: (LoginOrSignupEvent) -> Unit,
) {
    val loginViewModel: LoginViewModel = getViewModel()
    val signupViewModel: SignupViewModel = getViewModel()
    val loginUIState by loginViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val signupUIState by signupViewModel.uiStateFlow.collectAsStateWithLifecycle()
    FullSizeLoading(loginUIState == LoginUIState.Loading || (signupUIState as? SignupUIState.SignupForm)?.isLoading == true) {
        FullSizeWithLogo {
            Card(
                modifier = modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp)
                        .padding(top = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
//                        TwoButtonInRow(
//                            modifier = Modifier.fillMaxWidth(),
//                            fModifier = Modifier.weight(1F),
//                            sModifier = Modifier.weight(2F),
//                            firstText = stringResource(id = R.string.common_login),
//                            secondText = stringResource(id = R.string.signup),
//                            indexFocused = uiState.index,
//                            firstClick = {
//                                handleEvent(LoginOrSignupEvent.SwitchToLogin)
//                            },
//                            secondClick = {
//                                handleEvent(LoginOrSignupEvent.SwitchSignup)
//                            },
//                        )
//                        Spacer(modifier = Modifier.height(24.dp))

                        when (uiState) {
                            LoginOrSignupUIState.Login -> LoginScreen(
                                viewModel = loginViewModel,
                                savedStateHandle = savedStateHandle,
                            )

                            LoginOrSignupUIState.Signup -> SignupScreen(signupViewModel = signupViewModel)
                        }
                    },
                )
            }
        }
    }
}
