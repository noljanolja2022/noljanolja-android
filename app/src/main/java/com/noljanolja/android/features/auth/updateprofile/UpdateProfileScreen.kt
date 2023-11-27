package com.noljanolja.android.features.auth.updateprofile

import android.annotation.SuppressLint
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.common.country.Countries
import com.noljanolja.android.common.country.Country
import com.noljanolja.android.common.country.DEFAULT_CODE
import com.noljanolja.android.common.country.getFlagEmoji
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.features.auth.updateprofile.components.DoBInput
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.LoadingDialog
import com.noljanolja.android.ui.composable.OutlinedTextField
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.loadFileInfo
import com.noljanolja.android.util.toUri
import com.noljanolja.core.user.domain.model.Gender
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun UpdateProfileScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: UpdateProfileViewModel = getViewModel(),
) {
    val countryCode = savedStateHandle.get<String>("countryCode")
    savedStateHandle.remove<String>("countryCode")
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    UpdateProfileContent(
        countryCode = countryCode,
        uiState = uiState,
        userFlow = viewModel.userFlow,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileContent(
    countryCode: String?,
    uiState: UpdateProfileUiState,
    userFlow: StateFlow<User?>,
    handleEvent: (UpdateProfileEvent) -> Unit,
) {
    var showErrorPhoneNumber by remember { mutableStateOf(false) }
    val genders =
        remember { mutableStateListOf(Gender.MALE.name, Gender.FEMALE.name, Gender.OTHER.name) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    val maxNameLength = rememberSaveable { 20 }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf<String?>(null) }
    var dob by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    val isRegisterEnable = name.isNotBlank()
    val selectCountry by remember {
        mutableStateOf(
            Countries.find {
                it.nameCode == countryCode
            }
        )
    }
    var country by remember {
        mutableStateOf(
            Countries.first {
                it.nameCode == (countryCode ?: DEFAULT_CODE)
            }
        )
    }
    LaunchedEffect(userFlow) {
        userFlow.collectLatest {
            it?.let { user ->
                val phoneAndCode =
                    convertPhoneAndCode(selectCountry?.phoneCode, user.phone.orEmpty())
                avatar = user.avatar?.toUri()
                name = user.name
                phone = phoneAndCode.second
                gender = user.gender?.name
                dob = user.dob?.toJavaLocalDate()
                country = phoneAndCode.first
            }
        }
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val countryInteractionSource = remember { MutableInteractionSource() }
    if (countryInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { focusManager.clearFocus(true) }
                ),
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SizeBox(height = 20.dp)
                Box(
                    modifier = Modifier.size(123.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    val imageModifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { showAvatarInputDialog = true }
                    avatar?.let {
                        SubcomposeAsyncImage(
                            avatar,
                            contentDescription = null,
                            modifier = imageModifier,
                            contentScale = ContentScale.Crop,
                        )
                    } ?: Image(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = imageModifier,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
                    )
                    Image(
                        Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showAvatarInputDialog = true },

                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "User information",
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                SizeBox(height = 8.dp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (name.isNotBlank()) {
                            IconButton(onClick = {
                                name = ""
                            }) {
                                Icon(Icons.Outlined.Cancel, contentDescription = null)
                            }
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onBackground
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                SizeBox(height = 8.dp)

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        if (phone.isNotBlank()) {
                            Text("Phone")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                handleEvent(UpdateProfileEvent.OpenCountryList)
                            }
                        ) {
                            Text(
                                text = country.getFlagEmoji(),
                                modifier = Modifier.padding(horizontal = 7.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(5.dp)
                            )
                            Text(text = "+${country.phoneCode}")
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }

                    },
                    trailingIcon = {
                        if (phone.isNotBlank()) {
                            IconButton(onClick = {
                                phone = ""
                            }) {
                                Icon(Icons.Outlined.Cancel, contentDescription = null)
                            }
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )

                SizeBox(height = 8.dp)
                DoBInput(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = focusManager,
                    label = stringResource(R.string.update_profile_dob),
                    dob = dob,
                    onDoBChange = { dob = it }
                )
                SizeBox(height = 8.dp)
                Text(
                    text = stringResource(R.string.update_profile_gender),
                    style = MaterialTheme.typography.bodyLarge.withBold(),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                SizeBox(height = 12.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    genders.forEachIndexed { index, value ->
                        if (index > 0) {
                            SizeBox(width = 12.dp)
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (gender == value) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ).background(
                                    if (gender == value) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.background
                                    }
                                )
                                .clickable {
                                    gender = value
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = value, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(54.dp))
                PrimaryButton(
                    onClick = {
                        val fileInfo = avatar?.let { context.loadFileInfo(it) }
                        val formattedPhoneNumber =
                            PhoneNumberUtils.formatNumberToE164(
                                phone.trim(),
                                country.nameCode.uppercase()
                            )
                        if (formattedPhoneNumber == null) {
                            showErrorPhoneNumber = true
                        } else {
                            handleEvent(
                                UpdateProfileEvent.Update(
                                    name.trim(),
                                    formattedPhoneNumber,
                                    dob?.toKotlinLocalDate(),
                                    gender?.let { Gender.valueOf(it) },
                                    files = fileInfo?.contents,
                                    fileName = fileInfo?.name,
                                    fileType = fileInfo?.contentType.orEmpty()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    isEnable = name.isNotBlank() && phone.isNotBlank(),
                    text = stringResource(id = R.string.common_ok).uppercase()
                )
            }
        }
    }

    AvatarInput(
        isShown = showAvatarInputDialog,
        onAvatarInput = { uri ->
            uri?.let { avatar = it }
            showAvatarInputDialog = false
        },
    )

    LoadingDialog(
        title = stringResource(R.string.common_continue),
        isLoading = uiState.loading,
    )
    uiState.error?.let {
        ErrorDialog(
            showError = true,
            title = stringResource(R.string.common_error_title),
            description = it.message
                ?: stringResource(R.string.common_error_description),
            onDismiss = { handleEvent(UpdateProfileEvent.DismissError) }
        )
    }

    ErrorDialog(
        showError = showErrorPhoneNumber,
        title = stringResource(R.string.login_invalid_phone_title),
        description = stringResource(R.string.login_invalid_phone_description)
    ) {
        showErrorPhoneNumber = false
    }
}

private fun convertPhoneAndCode(code: String?, phone: String): Pair<Country, String> {
    val phoneCode =
        Countries.find { phone.startsWith("+${it.phoneCode}") }?.phoneCode ?: DEFAULT_CODE
    return Countries.first { it.phoneCode == (code ?: phoneCode) } to phone.replace(
        "+$phoneCode",
        ""
    )
}