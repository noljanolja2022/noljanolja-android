package com.noljanolja.android.features.auth.forget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.features.auth.common.component.FullSizeWithLogo
import com.noljanolja.android.features.auth.common.component.RoundedTextField
import com.noljanolja.android.ui.composable.FullSizeLoading
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SecondaryButton
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun ForgotScreen(viewModel: ForgotViewModel = getViewModel()) {
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
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    FullSizeWithLogo(
        onBack = {
            viewModel.handleEvent(ForgotEvent.Close)
        },
    ) {
        ForgotContent(
            uiState = uiState,
            handleEvent = {
                viewModel.handleEvent(it)
            },
        )
    }
}

@Composable
fun ForgotContent(
    uiState: ForgotUIState,
    handleEvent: (ForgotEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(
                        vertical = 24.dp,
                        horizontal = 20.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ForgotHeader()
                when (uiState) {
                    is ForgotUIState.Normal -> {
                        ForgotForm(
                            uiState = uiState,
                            onChangeEmail = {
                                handleEvent(ForgotEvent.ChangeEmail(it))
                            },
                            onBack = {
                                handleEvent(ForgotEvent.Back)
                            },
                            onSubmit = {
                                handleEvent(ForgotEvent.VerifyEmail)
                            },
                        )
                    }
                    is ForgotUIState.VerifyCompleted -> {
                        ResendEmailComponent(
                            onResendPassword = {
                                handleEvent(ForgotEvent.ResendPassword)
                            },
                            onBack = {
                                handleEvent(ForgotEvent.Back)
                            },
                        )
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
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val email = uiState.email
    Spacer(modifier = Modifier.height(20.dp))
    RoundedTextField(
        value = email,
        hint = stringResource(id = R.string.email_hint_text),
        onValueChange = onChangeEmail,
    )
    Spacer(modifier = Modifier.weight(1F))
    Row(modifier = Modifier.fillMaxWidth()) {
        SecondaryButton(
            modifier = Modifier.weight(1F),
            text = stringResource(id = R.string.common_previous),
            isEnable = true,
            onClick = onBack,
        )
        Spacer(modifier = Modifier.width(12.dp))
        PrimaryButton(
            modifier = Modifier.weight(1F),
            text = stringResource(id = R.string.auth_email_verification),
            isEnable = email.isNotBlank(),
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick = onSubmit,
        )
    }
}

@Composable
fun ColumnScope.ResendEmailComponent(
    onResendPassword: () -> Unit,
    onBack: () -> Unit,
) {
    Spacer(
        modifier = Modifier
            .heightIn(min = 56.dp, max = 86.dp),
    )
    Text(
        stringResource(id = R.string.auth_reset_email_sended),
        style = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.W700,
        ),
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = Modifier
            .padding(vertical = 28.dp)
            .fillMaxWidth(),
    ) {
        Text(
            stringResource(id = R.string.auth_login_new_password),
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
        )
    }

    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.auth_resend_password),
        containerColor = MaterialTheme.colorScheme.tertiary,
        onClick = onResendPassword,
    )
    Spacer(modifier = Modifier.weight(1F))
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.common_login),
        onClick = onBack,
    )
}

@Composable
fun ForgotHeader() {
    Text(
        stringResource(id = R.string.auth_forgot_title),
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.W700,
        ),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Preview
@Composable
private fun ForgotScreenPreview() {
    ForgotScreen()
}
