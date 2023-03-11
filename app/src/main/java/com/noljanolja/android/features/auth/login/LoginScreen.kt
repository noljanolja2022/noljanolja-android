package com.noljanolja.android.features.auth.login

import android.content.Intent
import android.telephony.PhoneNumberUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.core.country.domain.model.Countries
import com.noljanolja.core.country.domain.model.Country
import com.noljanolja.android.features.auth.common.component.EmailAndPassword
import com.noljanolja.android.features.auth.common.component.VerifyEmail
import com.noljanolja.android.features.auth.login.component.LoginButton
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SecondaryButton
import com.noljanolja.android.util.PrefixTransformation

@Composable
fun LoginScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val countryCode = savedStateHandle.get<String>("countryCode")
    val context = LocalContext.current
    viewModel.handleError()
    val email by viewModel.emailFlow.collectAsStateWithLifecycle()
    val password by viewModel.passwordFlow.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val googleLauncher = rememberAuthLauncher {
//        viewModel.handleLoginWithGoogleFromIntent(it)
    }
    val naverLauncher = rememberAuthLauncher {
//        viewModel.handleLoginWithNaverFromIntent(it)
    }
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

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
                countryCode = countryCode,
                email = email,
                password = password,
                emailError = emailError,
                passwordError = passwordError,
                handleEvent = viewModel::handleEvent,
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

@Composable
private fun LoginContent(
    countryCode: String?,
    email: String,
    password: String,
    emailError: Throwable?,
    passwordError: Throwable?,
    handleEvent: (LoginEvent) -> Unit,
    onLoginGoogle: () -> Unit,
    onLoginNaver: () -> Unit,
) {
    val country by remember {
        mutableStateOf(
            Countries.first {
                it.nameCode == (countryCode ?: "vn")
            }
        )
    }
    var phone by rememberSaveable { mutableStateOf("") }
    var showErrorPhoneNumber by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginPhoneContent(
            country = country,
            phone = phone,
            onChangePhone = {
                phone = it
            },
            openCountryList = {
                handleEvent(LoginEvent.OpenCountryList)
            },
            onSubmit = {
                val formattedPhoneNumber =
                    PhoneNumberUtils.formatNumberToE164(phone.trim(), country.nameCode.uppercase())
                if (formattedPhoneNumber == null) {
                    showErrorPhoneNumber = true
                } else {
                    handleEvent(LoginEvent.SendOTP(formattedPhoneNumber))
                }
            }
        )
//    LoginEmailContent(
//        email = email,
//        password = password,
//        emailError = emailError,
//        passwordError = passwordError,
//        handleEvent = handleEvent
//    )

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
//        LoginSNSButton(painter = painterResource(id = R.drawable.kakao), onClick = {
//            handleEvent(LoginEvent.LoginKakao)
//        })
//        Spacer(modifier = Modifier.width(24.dp))
//        LoginSNSButton(painter = painterResource(id = R.drawable.naver), onClick = onLoginNaver)
//        Spacer(modifier = Modifier.width(24.dp))
            LoginSNSButton(
                painter = painterResource(id = R.drawable.google),
                onClick = onLoginGoogle,
            )
        }
        Spacer(modifier = Modifier.weight(1F))
    }

    ErrorDialog(
        showError = showErrorPhoneNumber,
        title = stringResource(R.string.login_invalid_phone_title),
        description = stringResource(R.string.login_invalid_phone_description)
    ) {
        showErrorPhoneNumber = false
    }
}

@Composable
private fun ColumnScope.LoginEmailContent(
    email: String,
    password: String,
    emailError: Throwable?,
    passwordError: Throwable?,
    handleEvent: (LoginEvent) -> Unit,
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
}

@Composable
private fun ColumnScope.LoginPhoneContent(
    country: Country,
    phone: String,
    onChangePhone: (String) -> Unit,
    openCountryList: () -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val countryInteractionSource = remember { MutableInteractionSource() }
    if (countryInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
        openCountryList.invoke()
    }
    Text(
        stringResource(R.string.login_title),
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
        style = MaterialTheme.typography.titleLarge
    )

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        TextField(
            modifier = Modifier.width(130.dp).wrapContentHeight(),
            label = {
                Text(
                    stringResource(R.string.login_country_input_label),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            value = country.name,
            onValueChange = { },
            singleLine = true,
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            interactionSource = countryInteractionSource
        )

        TextField(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            label = {
                Text(
                    stringResource(R.string.login_phone_input_label),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            value = phone,
            onValueChange = onChangePhone,
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            singleLine = true,
            visualTransformation = PrefixTransformation("+${country.phoneCode} "),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) })
        )
    }
    LoginButton(
        modifier = Modifier.padding(top = 28.dp),
        isEnable = phone.isNotBlank(),
        onClick = onSubmit
    )
}

@Composable
fun LoginVerifyEmail(
    onBack: () -> Unit,
    onVerify: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
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
