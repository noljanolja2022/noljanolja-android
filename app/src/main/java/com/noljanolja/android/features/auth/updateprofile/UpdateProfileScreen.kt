package com.noljanolja.android.features.auth.updateprofile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.features.auth.updateprofile.components.DoBInput
import com.noljanolja.android.features.auth.updateprofile.components.GenderInput
import com.noljanolja.android.features.auth.updateprofile.components.NameInput
import com.noljanolja.android.features.auth.updateprofile.components.PhoneInput
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.LoadingDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
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
    viewModel: UpdateProfileViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    UpdateProfileContent(
        uiState = uiState,
        userFlow = viewModel.userFlow,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileContent(
    uiState: UpdateProfileUiState,
    userFlow: StateFlow<User?>,
    handleEvent: (UpdateProfileEvent) -> Unit,
) {
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

    LaunchedEffect(userFlow) {
        userFlow.collectLatest {
            it?.let { user ->
                avatar = user.avatar?.toUri()
                name = user.name
                phone = user.phone.orEmpty()
                gender = user.gender?.name
                dob = user.dob?.toJavaLocalDate()
            }
        }
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

//    when (registerViewState) {
//        is ViewState.Error -> {
//            loading = false
//            error = registerViewState.cause
//        }
//        is ViewState.Loading -> {
//            loading = true
//            error = null
//        }
//        is ViewState.Idle -> {
//            loading = false
//            error = null
//        }
//        else -> {
//            loading = false
//            error = null
//        }
//    }

//    LaunchedEffect(avatar) {
//        avatar?.let {
//            context.contentResolver.openInputStream(it)?.let {
//                handleEvent(UpdateProfileEvent.UploadAvatar(it.readBytes()))
//            }
//        }
//    }

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Box(
                    modifier = Modifier.size(106.dp),
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

                Spacer(modifier = Modifier.height(36.dp))

                NameInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    label = stringResource(R.string.update_profile_name),
                    name = name,
                    maxNameLength = maxNameLength,
                    onNameChange = { if (it.trim().length <= maxNameLength) name = it }
                )
                SizeBox(height = 8.dp)
                PhoneInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    label = stringResource(R.string.common_phone),
                    phone = phone,
                    onPhoneChange = { phone = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DoBInput(
                        modifier = Modifier.weight(1f),
                        focusManager = focusManager,
                        label = stringResource(R.string.update_profile_dob),
                        dob = dob,
                        onDoBChange = { dob = it }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    GenderInput(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.update_profile_gender),
                        gender = gender,
                        genders = genders,
                        onGenderChange = { gender = it },
                    )
                }

                Spacer(modifier = Modifier.height(54.dp))
                PrimaryButton(
                    onClick = {
                        val fileInfo = avatar?.let { context.loadFileInfo(it) }
                        handleEvent(
                            UpdateProfileEvent.Update(
                                name.trim(),
                                phone,
                                dob?.toKotlinLocalDate(),
                                gender?.let { Gender.valueOf(it) },
                                files = fileInfo?.contents,
                                fileName = fileInfo?.name,
                                fileType = fileInfo?.contentType.orEmpty()
                            )
                        )
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
}