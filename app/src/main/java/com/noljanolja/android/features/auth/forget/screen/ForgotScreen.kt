package com.noljanolja.android.features.auth.forget.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.common.composable.TwoButtonInRow
import com.noljanolja.android.features.auth.common.component.FullSizeWithLogo
import com.noljanolja.android.features.auth.common.component.RoundedTextField
import com.noljanolja.android.features.auth.login.screen.component.RoundedButton
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.launch

@Composable
fun ForgotScreen(viewModel: ForgotViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.errorFlow) {
        launch {
            viewModel.errorFlow.collect {
                with(context) {
                    showToast(getErrorMessage(it))
                }
            }
        }
    }
    val uiState by viewModel.uiStateFlow.collectAsState()
    FullSizeWithLogo {
        ForgotContent(
            uiState = uiState,
            handleEvent = {
                viewModel.handleEvent(it)
            }
        )
    }
}

@Composable
fun ForgotContent(
    uiState: ForgotUIState,
    handleEvent: (ForgotEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(
                        vertical = 24.dp,
                        horizontal = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ForgotHeader {
                    handleEvent(ForgotEvent.Back)
                }
                Spacer(modifier = Modifier.height(20.dp))
                TwoButtonInRow(
                    firstText = stringResource(id = R.string.auth_find_email),
                    secondText = stringResource(id = R.string.auth_find_password),
                    indexFocused = 1,
                    firstClick = { },
                    secondClick = {
                    },
                )
                when (uiState) {
                    is ForgotUIState.Normal -> {
                        ForgotForm(
                            uiState = uiState,
                            onChangeEmail = {
                                handleEvent(ForgotEvent.ChangeEmail(it))
                            }, onSubmit = {
                                handleEvent(ForgotEvent.VerifyEmail)
                            }
                        )
                    }
                    is ForgotUIState.VerifyCompleted -> {
                        VerificationComplete {
                            handleEvent(ForgotEvent.NextVerification)
                        }
                    }
                    is ForgotUIState.ResendPassword -> {
                        ResendEmailComponent {
                            handleEvent(ForgotEvent.ResendPassword)
                        }
                    }
                }
            }
        }
        if ((uiState as? ForgotUIState.Normal)?.isLoading == true) {
            FullSizeLoading()
        }
    }
}

@Composable
private fun ColumnScope.ForgotForm(
    uiState: ForgotUIState.Normal,
    onChangeEmail: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val email = uiState.email
    Spacer(modifier = Modifier.height(20.dp))
    RoundedTextField(
        value = email,
        hint = stringResource(id = R.string.email_hint_text),
        onValueChange = onChangeEmail
    )
    Spacer(modifier = Modifier.weight(1F))
    RoundedButton(
        text = stringResource(id = R.string.auth_email_verification),
        isEnable = email.isNotBlank(),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary_text_color),
            disabledContainerColor = colorResource(id = R.color.background),
            contentColor = Color.White,
            disabledContentColor = colorResource(id = R.color.disable_text)
        ),
        onClick = onSubmit
    )
}

@Composable
fun ColumnScope.VerificationComplete(
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.weight(1F))
    Image(
        painterResource(id = R.drawable.ic_check_circle),
        contentDescription = null,
        modifier = Modifier.size(62.dp)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
        stringResource(id = R.string.auth_email_verification_complete),
        style = TextStyle(
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            color = colorResource(id = R.color.primary_text_color)
        )
    )
    Spacer(modifier = Modifier.weight(1F))
    RoundedButton(
        text = stringResource(id = R.string.common_next),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary_text_color),
            contentColor = Color.White,
        ),
        onClick = onNext
    )
}

@Composable
fun ColumnScope.ResendEmailComponent(
    onResendPassword: () -> Unit
) {
    Spacer(
        modifier = Modifier
            .heightIn(min = 56.dp, max = 86.dp)
    )
    Text(
        stringResource(id = R.string.auth_reset_email_sended),
        style = TextStyle(
            fontSize = 18.sp,
            color = colorResource(id = R.color.primary_text_color),
            fontWeight = FontWeight.W700
        ),
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.background)
        ),
        modifier = Modifier
            .padding(vertical = 28.dp)
            .fillMaxWidth()
    ) {
        Text(
            stringResource(id = R.string.auth_login_new_password),
            style = TextStyle(
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_text_color)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }

    RoundedButton(
        text = stringResource(id = R.string.auth_resend_password),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.orange),
            contentColor = Color.White,
        ),
        onClick = onResendPassword
    )
    Spacer(modifier = Modifier.weight(1F))
}

@Composable
fun ForgotHeader(
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colorResource(id = R.color.primary_text_color)
            )
        }
        Text(
            stringResource(id = R.string.auth_forgot_title),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = colorResource(id = R.color.primary_text_color),
                fontWeight = FontWeight.W700
            ),
            modifier = Modifier.weight(1F)
        )
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Preview
@Composable
private fun ForgotScreenPreview() {
    ForgotScreen()
}