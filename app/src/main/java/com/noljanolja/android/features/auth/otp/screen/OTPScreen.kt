package com.noljanolja.android.features.auth.otp.screen

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.features.auth.otp.composable.OTPRow
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.LoadingDialog
import kotlinx.coroutines.delay

private const val BLOCK_RESEND_TIME = 90_000
private const val ONE_MILI_SECOND = 1_000

@Composable
fun OTPScreen(
    phone: String,
    viewModel: OTPViewModel = hiltViewModel()
) {
    val otpUIState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    OTPScreenContent(
        otpUIState = otpUIState,
        phone = phone,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreenContent(
    otpUIState: OTPUIState,
    phone: String,
    handleEvent: (OTPEvent) -> Unit,
) {
    val context = LocalContext.current
    var otp by rememberSaveable { mutableStateOf(CharArray(6)) }
    var otpVerificationId by rememberSaveable { mutableStateOf("") }
    val error = otpUIState.error
    val loading = otpUIState.loading

    var currentTime by rememberSaveable { mutableStateOf(BLOCK_RESEND_TIME) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(currentTime) {
        if (currentTime == BLOCK_RESEND_TIME) {
            AuthSdk.loginWithPhone(
                context = context as Activity,
                phone = phone,
                timeout = (BLOCK_RESEND_TIME / ONE_MILI_SECOND).toLong(),
                onVerificationCompleted = { smsCode ->
                    val newOTP = otp.clone()
                    smsCode?.toCharArray()?.forEachIndexed { index, char ->
                        newOTP[index] = char
                    }
                    otp = newOTP
                },
                onCodeSent = {
                    otpVerificationId = it
                }
            )
        }
        if (currentTime > 0) {
            delay(1000)
            currentTime -= 1000
        }
    }

    LaunchedEffect(otp) {
        if (otp.all { it.isDigit() }) {
            handleEvent(OTPEvent.VerifyOTP(otpVerificationId, String(otp)))
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it)
            ) {
                Text(
                    stringResource(R.string.otp_title),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                            )
                        ) {
                            append(stringResource(R.string.otp_description))
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append(" $phone")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OTPRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    otp = otp,
                    onOTPChange = { position, char ->
                        otp = CharArray(6) {
                            if (it == position) char else otp[it]
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                if (currentTime > 0) {
                    Text(
                        stringResource(R.string.otp_resend_code_waiting, currentTime / 1000),
                        modifier = Modifier.padding(start = 16.dp, bottom = 24.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Text(
                        stringResource(R.string.otp_resend_code),
                        modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
                            .clickable { currentTime = BLOCK_RESEND_TIME },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

    LoadingDialog(
        title = stringResource(R.string.otp_verifying),
        isLoading = loading,
    )

    ErrorDialog(
        showError = error != null,
        title = stringResource(R.string.common_error_title),
        description = stringResource(R.string.common_error_description),
        onDismiss = { handleEvent(OTPEvent.DismissError) }
    )
}