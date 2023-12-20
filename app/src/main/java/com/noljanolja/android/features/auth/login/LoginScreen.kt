package com.noljanolja.android.features.auth.login

import android.app.*
import android.content.*
import androidx.activity.compose.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import co.touchlab.kermit.*
import com.d2brothers.firebase_auth.*
import com.google.firebase.remoteconfig.*
import com.noljanolja.android.R
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.country.*
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.auth.common.component.*
import com.noljanolja.android.features.auth.login.component.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VERTICAL_SCREEN
import com.noljanolja.android.util.Constant.DefaultValue.PADDING_VIEW_SCREEN
import org.koin.androidx.compose.*

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
    val formattedPhoneNumber by remember {
        derivedStateOf {
            phone.getPhoneNumberFormatE164(country.nameCode)
        }
    }

    ScaffoldWithCircleBgRoundedContent(
        heading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(115.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        },
        roundedCornerShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        backgroundBottomColor = MaterialTheme.shopBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = PADDING_VIEW_SCREEN.dp,
                    vertical = PADDING_VERTICAL_SCREEN.dp
                ),
            horizontalAlignment = Alignment.Start,
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
                    if (formattedPhoneNumber == null) {
                        showErrorPhoneNumber = true
                    } else {
                        showConfirmPhoneNumber = true
                    }
                }
            )
        }
    }

    ErrorDialog(
        showError = showErrorPhoneNumber,
        title = stringResource(R.string.login_invalid_phone_title),
        description = stringResource(R.string.login_invalid_phone_description)
    ) {
        showErrorPhoneNumber = false
    }

    WarningDialog(
        title = formattedPhoneNumber,
        content = stringResource(R.string.login_confirm_phone_description),
        dismissText = stringResource(R.string.common_cancel),
        confirmText = stringResource(R.string.common_confirm),
        isWarning = showConfirmPhoneNumber,
        onDismiss = {
            showConfirmPhoneNumber = false
        },
        onConfirm = {
            showConfirmPhoneNumber = false
            formattedPhoneNumber?.let { LoginEvent.SendOTP(it) }?.let { handleEvent(it) }
        }
    )


//    val googleLauncher = rememberAuthLauncher {
//        scope.launch {
//            val result = authSdk.getAccountFromGoogleIntent(it)
//            if (result.isSuccess) {
//                handleEvent(LoginEvent.HandleLoginResult(result.getOrDefault("")))
//            } else {
//                context.showError(result.exceptionOrNull() ?: UnexpectedFailure)
//            }
//        }
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .verticalScroll(rememberScrollState())
//            .padding(horizontal = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = null,
//            modifier = Modifier
//                .padding(horizontal = 90.dp, vertical = 50.dp)
//                .fillMaxWidth(),
//            contentScale = ContentScale.FillWidth
//        )
//        Text(
//            stringResource(id = R.string.common_login),
//            style = MaterialTheme.typography.displaySmall.withBold()
//        )
//        Text(
//            stringResource(R.string.login_google_description),
//            modifier = Modifier.fillMaxWidth(),
//            style = MaterialTheme.typography.bodyMedium,
//            textAlign = TextAlign.Center
//        )
//        SizeBox(height = 20.dp)
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .padding()
//                .clip(RoundedCornerShape(8.dp))
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.background)
//                .padding(horizontal = 12.dp, vertical = 4.dp)
//                .clickable {
//                    AuthSdk.authenticateGoogle(context, googleLauncher)
//                },
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = painterResource(R.drawable.google),
//                contentDescription = null,
//                modifier = Modifier.size(30.dp)
//            )
//            SizeBox(width = 8.dp)
//            Text(
//                stringResource(R.string.login_google_button),
//                color = MaterialTheme.colorScheme.onBackground,
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//
//        LoginUserNamePasswordContent(
//            onLogin = { user, password ->
//                scope.launch {
//                    val result =
//                        authSdk.signInWithEmailAndPassword(email = user, password = password)
//                    if (result.isSuccess) {
//                        handleEvent(LoginEvent.HandleLoginResult(result.getOrDefault("")))
//                    } else {
//                        context.showError(result.exceptionOrNull() ?: UnexpectedFailure)
//                    }
//                }
//            }
//        )
//
//    }
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
    MarginVertical(20)
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
        Text(
            text = "---------- Or ----------",
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
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
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,

                )
        )
        SizeBox(height = 25.dp)
        PrimaryButton(
            text = stringResource(id = R.string.common_login),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            onLogin(username.trim(), password.trim())
        }
        SizeBox(height = 25.dp)
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
