package com.noljanolja.android.features.auth.login

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.d2brothers.firebase_auth.AuthSdk
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.country.Countries
import com.noljanolja.android.common.country.Country
import com.noljanolja.android.common.country.DEFAULT_CODE
import com.noljanolja.android.common.country.getFlagEmoji
import com.noljanolja.android.common.error.UnexpectedFailure
import com.noljanolja.android.features.auth.common.component.VerifyEmail
import com.noljanolja.android.features.auth.login.component.LoginButton
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SecondaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.showError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: LoginViewModel = getViewModel(),
) {
    val countryCode = savedStateHandle.get<String>("countryCode")
    val context = LocalContext.current
    viewModel.handleError()
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
                handleEvent = viewModel::handleEvent,
            )
        }
    }
}

@Composable
private fun LoginContent(
    countryCode: String?,
    handleEvent: (LoginEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val authSdk = AuthSdk.instance
    val context = LocalContext.current
    val country by remember {
        mutableStateOf(
            Countries.first {
                it.nameCode == (countryCode ?: DEFAULT_CODE)
            }
        )
    }
    var phone by rememberSaveable { mutableStateOf("") }
    var showErrorPhoneNumber by remember { mutableStateOf(false) }
    var showConfirmPhoneNumber by remember { mutableStateOf(false) }

    val googleLauncher = rememberAuthLauncher {
        scope.launch {
            val result = authSdk.getAccountFromGoogleIntent(it)
            if (result.isSuccess) {
                handleEvent(LoginEvent.HandleLoginResult(result.getOrDefault("")))
            } else {
                context.showError(result.exceptionOrNull() ?: UnexpectedFailure)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            stringResource(id = R.string.common_login),
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            stringResource(R.string.login_google_description),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
        Expanded()
        LoginUserNamePasswordContent(
            onLogin = { user, password ->
                scope.launch {
                    val result =
                        authSdk.signInWithEmailAndPassword(email = user, password = password)
                    if (result.isSuccess) {
                        handleEvent(LoginEvent.HandleLoginResult(result.getOrDefault("")))
                    } else {
                        context.showError(result.exceptionOrNull() ?: UnexpectedFailure)
                    }
                }
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable {
                    AuthSdk.authenticateGoogle(context, googleLauncher)
                },
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            SizeBox(width = 8.dp)
            Text(
                stringResource(R.string.login_google_button),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Expanded()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        stringResource(id = R.string.common_login),
        style = MaterialTheme.typography.displaySmall
    )
    Text(
        stringResource(R.string.login_phone_description),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(15.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.widthIn(1.dp, 120.dp),
            value = "${country.getFlagEmoji()} +${country.phoneCode}",
            onValueChange = { },
            singleLine = true,
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            interactionSource = countryInteractionSource,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )

        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            value = phone,
            onValueChange = onChangePhone,
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )
    }
    Spacer(modifier = Modifier.weight(1F))
    LoginButton(
        modifier = Modifier.padding(top = 28.dp),
        isEnable = phone.isNotBlank(),
        onClick = onSubmit
    )
    Spacer(modifier = Modifier.weight(1F))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginUserNamePasswordContent(onLogin: (user: String, password: String) -> Unit) {
    var enable by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    LaunchedEffect(true) {
        if (context !is Activity) return@LaunchedEffect
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    val value = remoteConfig.getBoolean("login_email_password")
                    enable = value
                    Logger.e("Fetch remote config: $value")
                } else {
                    Logger.e("Fetch remote config: ${task.exception}")
                }
            }
    }

    if (enable) {
        var username by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        SizeBox(height = 10.dp)
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        SizeBox(height = 25.dp)
        PrimaryButton(
            text = "Login",
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            onLogin(username.trim(), password.trim())
        }
        Text(
            text = "---------- Or ----------",
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
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
