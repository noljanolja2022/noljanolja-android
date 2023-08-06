package com.noljanolja.android.features.auth.login

import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.handleError
import com.noljanolja.android.common.country.Countries
import com.noljanolja.android.common.country.Country
import com.noljanolja.android.common.country.DEFAULT_CODE
import com.noljanolja.android.common.country.getFlagEmoji
import com.noljanolja.android.features.auth.common.component.VerifyEmail
import com.noljanolja.android.features.auth.login.component.LoginButton
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SecondaryButton
import com.noljanolja.android.ui.composable.WarningDialog
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

    Column(
        modifier = Modifier.fillMaxSize(),
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
                val formattedPhoneNumber =
                    PhoneNumberUtils.formatNumberToE164(phone.trim(), country.nameCode.uppercase())
                if (formattedPhoneNumber == null) {
                    showErrorPhoneNumber = true
                } else {
                    showConfirmPhoneNumber = true
                }
            }
        )
        Spacer(modifier = Modifier.weight(1F))
    }

    ErrorDialog(
        showError = showErrorPhoneNumber,
        title = stringResource(R.string.login_invalid_phone_title),
        description = stringResource(R.string.login_invalid_phone_description)
    ) {
        showErrorPhoneNumber = false
    }
    val formattedPhoneNumber =
        PhoneNumberUtils.formatNumberToE164(phone.trim(), country.nameCode.uppercase())

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
            handleEvent(LoginEvent.SendOTP(formattedPhoneNumber))
        }
    )
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
            modifier = Modifier.weight(1f).padding(start = 16.dp),
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