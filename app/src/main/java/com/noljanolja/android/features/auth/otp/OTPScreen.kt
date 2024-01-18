package com.noljanolja.android.features.auth.otp

import android.app.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import com.d2brothers.firebase_auth.*
import com.noljanolja.android.R
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.features.auth.otp.composable.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import kotlinx.coroutines.*
import org.koin.androidx.compose.*

private const val BLOCK_RESEND_TIME = 90_000L
private const val ONE_MILLI_SECOND = 1_000L

@Composable
fun OTPScreen(
    phone: String,
    viewModel: OTPViewModel = getViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.errorFlow, block = {
        viewModel.errorFlow.collect {
            context.showToast(it.message)
        }
    })
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
    val sharedPreferenceHelper: SharedPreferenceHelper = get()
    val context = LocalContext.current
    var otp by rememberSaveable { mutableStateOf(CharArray(6)) }
    var otpVerificationId by rememberSaveable { mutableStateOf("") }
    val error = otpUIState.error
    val loading = otpUIState.loading

    var currentTime by rememberSaveable {
        mutableStateOf(
            minOf(
                BLOCK_RESEND_TIME,
                sharedPreferenceHelper.loginOtpTime
            )
        )
    }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(currentTime) {
        if (currentTime == BLOCK_RESEND_TIME) {
            AuthSdk.loginWithPhone(
                context = context as Activity,
                phone = phone,
                timeout = (BLOCK_RESEND_TIME / ONE_MILLI_SECOND),
                onVerificationCompleted = { smsCode ->
                    val newOTP = otp.clone()
                    smsCode?.toCharArray()?.forEachIndexed { index, char ->
                        newOTP[index] = char
                    }
                    otp = newOTP
                    sharedPreferenceHelper.loginOtpTime = 0L
                },
                onError = {
                    context.showToast(it.message)
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

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.otp_title),
                    modifier = Modifier.padding(
                        end = 16.dp,
                        top = 100.dp,
                        bottom = 14.dp
                    ),
                    style = MaterialTheme.typography.headlineSmall,
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
                    modifier = Modifier.padding(horizontal = 30.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(35.dp))

                OTPRow(
                    focusManager = focusManager,
                    otp = otp,
                    onOTPChange = { position, char ->
                        otp = CharArray(6) {
                            if (it == position) char else otp[it]
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (currentTime > 0) {
                    Text(
                        stringResource(R.string.otp_resend_code_waiting, currentTime / 1000),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Text(
                        stringResource(R.string.otp_resend_code),
                        modifier = Modifier.clickable { currentTime = BLOCK_RESEND_TIME },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                ButtonRadius(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_VIEW_SCREEN.dp)
                        .height(48.dp),
                    title = stringResource(id = R.string.common_continue),
                    bgColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.Black,
                    enabled = otp.all { it.isDigit() },
                    onClick = {
                        if (otp.all { it.isDigit() }) {
                            handleEvent(OTPEvent.VerifyOTP(otpVerificationId, String(otp)))
                        }
                    }
                )

                MarginVertical(30)
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
        description = error?.message ?: stringResource(R.string.common_error_description),
        onDismiss = { handleEvent(OTPEvent.DismissError) }
    )
}