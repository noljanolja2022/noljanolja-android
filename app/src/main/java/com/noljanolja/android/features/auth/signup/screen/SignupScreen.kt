package com.noljanolja.android.features.auth.signup.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.composable.BackHandler
import com.noljanolja.android.common.composable.PrimaryButton
import com.noljanolja.android.common.composable.SecondaryButton
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.common.component.RoundedTextField
import com.noljanolja.android.features.auth.common.component.VerifyEmail
import com.noljanolja.android.features.auth.signup.screen.component.AgreementRow
import com.noljanolja.android.features.auth.signup.screen.component.FullAgreement

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = hiltViewModel(),
) {
    signupViewModel.handleError()
    val uiState by signupViewModel.uiStateFlow.collectAsState()
    val email by signupViewModel.emailFlow.collectAsState()
    val password by signupViewModel.passwordFlow.collectAsState()
    val emailError by signupViewModel.emailError.collectAsState()
    val passwordError by signupViewModel.passwordError.collectAsState()
    val confirmPassword by signupViewModel.confirmPasswordFlow.collectAsState()
    BackHandler(uiState !is SignupUIState.Agreement) {
        signupViewModel.handleEvent(SignupEvent.Back)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SignupProgress(uiState = uiState)
        when (uiState) {
            is SignupUIState.Agreement -> {
                SignupAgreement(
                    uiState = uiState as SignupUIState.Agreement,
                    handleEvent = {
                        signupViewModel.handleEvent(it)
                    },
                )
            }
            is SignupUIState.SignupForm -> {
                SignupForm(
                    email,
                    password,
                    emailError,
                    passwordError,
                    confirmPassword,
                ) {
                    signupViewModel.handleEvent(it)
                }
            }
            is SignupUIState.VerificationEmail -> {
                SignupVerification()
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        SignupActions(
            uiState = uiState,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            handleEvent = {
                signupViewModel.handleEvent(it)
            },
        )
    }
}

@Composable
private fun ColumnScope.SignupProgress(
    uiState: SignupUIState,
) {
    val step: String
    val stepDescription: String
    val progress: Float
    when (uiState) {
        is SignupUIState.Agreement -> {
            step = stringResource(id = R.string.auth_signup_step1)
            stepDescription = stringResource(id = R.string.auth_need_agreement_terms)
            progress = 0.33F
        }
        is SignupUIState.SignupForm -> {
            step = stringResource(id = R.string.auth_signup_step2)
            stepDescription = stringResource(id = R.string.auth_signup_with_email)
            progress = 0.66F
        }
        SignupUIState.VerificationEmail -> {
            step = stringResource(id = R.string.auth_signup_step3)
            stepDescription = stringResource(id = R.string.auth_verify_email_finish)
            progress = 1F
        }
    }
    Text(
        step,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
        ),
        modifier = Modifier.align(Alignment.Start),
    )
    Text(
        text = stepDescription,
        style = TextStyle(
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
        ),
        modifier = Modifier.align(Alignment.Start),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(progress / 0.33F))
        Image(painter = painterResource(id = R.drawable.ic_bicycle), contentDescription = null)
        (1 - progress).takeIf { it > 0 }?.let {
            Spacer(modifier = Modifier.weight(it / 0.33F))
        }
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
        color = MaterialTheme.colorScheme.primary,
        progress = progress,
    )

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun ColumnScope.SignupForm(
    email: String,
    password: String,
    emailError: Throwable?,
    passwordError: Throwable?,
    confirmPassword: String,
    handleEvent: (SignupEvent) -> Unit,
) {
    EmailAndPassword(
        email = email,
        password = password,
        emailError = emailError,
        passwordError = passwordError,
        onEmailChange = {
            handleEvent(SignupEvent.ChangeEmail(it))
        },
        onPasswordChange = {
            handleEvent(SignupEvent.ChangePassword(it))
        },
    )
    Spacer(modifier = Modifier.height(12.dp))
    RoundedTextField(
        value = confirmPassword,
        hint = stringResource(id = R.string.confirm_password_hint_text),
        hideText = true,
        onValueChange = {
            handleEvent(SignupEvent.ChangeConfirmPassword(it))
        },
    )
}

@Composable
private fun ColumnScope.SignupAgreement(
    uiState: SignupUIState.Agreement,
    handleEvent: (SignupEvent) -> Unit,
) {
    Box(modifier = Modifier.weight(100F)) {
        LazyColumn {
            item {
                FullAgreement(checked = uiState.agreements.all { it.checked }) {
                    handleEvent(SignupEvent.ToggleAllAgreement)
                }
                Divider(
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            uiState.agreements.forEach {
                item {
                    AgreementRow(
                        checked = it.checked,
                        tag = it.tag,
                        description = it.description,
                        onToggle = {
                            handleEvent(SignupEvent.ToggleAgreement(it.id))
                        },
                        onGoDetail = {
                            handleEvent(SignupEvent.GoTermsOfService(it.id))
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SignupVerification() {
    VerifyEmail()
}

@Composable
private fun ColumnScope.SignupActions(
    uiState: SignupUIState,
    email: String,
    password: String,
    confirmPassword: String,
    handleEvent: (SignupEvent) -> Unit,
) {
    Spacer(modifier = Modifier.weight(1F))
    Row {
        when (uiState) {
            is SignupUIState.Agreement -> {
                SignupRoundedButton(
                    text = stringResource(id = R.string.common_next),
                    enable = uiState.agreements.all { it.checked },
                ) {
                    handleEvent(SignupEvent.Next)
                }
            }
            is SignupUIState.SignupForm -> {
                SignupOutlineButton(text = stringResource(id = R.string.common_previous)) {
                    handleEvent(SignupEvent.Back)
                }
                Spacer(modifier = Modifier.width(12.dp))
                SignupRoundedButton(
                    text = stringResource(id = R.string.common_next),
                    enable = isEnableSignup(email, password, confirmPassword),
                ) {
                    handleEvent(SignupEvent.Next)
                }
            }
            SignupUIState.VerificationEmail -> {
                SignupOutlineButton(text = stringResource(id = R.string.common_previous)) {
                    handleEvent(SignupEvent.Back)
                }
                Spacer(modifier = Modifier.width(12.dp))
                SignupRoundedButton(text = stringResource(id = R.string.common_verification)) {
                    handleEvent(SignupEvent.Next)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun RowScope.SignupRoundedButton(
    text: String,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    PrimaryButton(
        modifier = Modifier.weight(1F),
        text = text,
        isEnable = enable,
        onClick = onClick,
    )
}

@Composable
private fun RowScope.SignupOutlineButton(
    text: String,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    SecondaryButton(
        modifier = Modifier.weight(1F),
        text = text,
        isEnable = enable,
        onClick = onClick,
    )
}

private fun isEnableSignup(
    email: String,
    password: String,
    confirmPassword: String,
) = email.isNotBlank() && password.isNotBlank() && password == confirmPassword
